package com.vaadin.showcase.views.products;

import java.util.stream.Stream;

import javax.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.showcase.data.entity.Products;
import com.vaadin.showcase.data.service.ProductsService;
import com.vaadin.showcase.views.MainLayout;

@PageTitle("Products")
@Route(value = "products/:productsID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@PermitAll
public class ProductsView extends Div {

    // BeforeEnterObserver (edit item by URL) id broken in vaadin-crud
    // https://github.com/vaadin/flow-components/issues/2815
    // private final String PRODUCTS_ID = "productsID";
    // private final String PRODUCTS_EDIT_ROUTE_TEMPLATE = "products/%s/edit";

    private Crud<Products> crud;
    private TextField name;
    private TextField ean;
    private TextField initial;
    private DatePicker received;
    private TextField current;
    private Checkbox inStock;

    private FormLayout form;
    private BeanValidationBinder<Products> binder;
    private ProductsService productsService;

    @Autowired
    public ProductsView(ProductsService productsService) {
        this.productsService = productsService;
        addClassNames("products-view", "flex", "flex-col", "h-full");

        // Configure Form
        binder = new BeanValidationBinder<>(Products.class);
        // Create Form content
        createEditorLayout();
        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(initial).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("initial");
        binder.forField(current).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("current");

        binder.bindInstanceFields(this);

        crud = new Crud<>(Products.class, new CrudGrid<>(Products.class, false), new BinderCrudEditor<>(binder, form));

        crud.setDataProvider(new AbstractBackEndDataProvider<Products, CrudFilter>() {
            protected Stream<Products> fetchFromBackEnd(Query<Products, CrudFilter> query) {
                return productsService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream();
            }

            protected int sizeInBackEnd(Query<Products, CrudFilter> query) {
                return productsService.count();
            }
        });
        crud.addDeleteListener(e -> {
            productsService.delete(e.getItem().getId());
        });
        crud.addSaveListener(e -> {
            Products value = e.getItem();

            productsService.update(value);
        });
        crud.addEditListener(e -> {
            Products value = e.getItem();

        });

        crud.setHeightFull();
        crud.setEditOnClick(true);
        add(crud);
    }

    private void createEditorLayout() {
        name = new TextField("Name");
        ean = new TextField("Ean");
        initial = new TextField("Initial");
        received = new DatePicker("Received");
        current = new TextField("Current");
        inStock = new Checkbox("In Stock");
        Component[] fields = new Component[] { name, ean, initial, received, current, inStock };

        form = new FormLayout(fields);

    }

}
