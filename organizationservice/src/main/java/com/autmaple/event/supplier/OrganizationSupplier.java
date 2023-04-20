package com.autmaple.event.supplier;

import com.autmaple.event.model.OrganizationChangeModel;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

//@Component
public class OrganizationSupplier implements Supplier<OrganizationChangeModel> {

    @Override
    public OrganizationChangeModel get() {
        System.out.println("=============");
        OrganizationChangeModel model = new OrganizationChangeModel();
        model.setOrganizationId("aaa-bbb-ccc-ddd");
        model.setCorrelationId("eee-fff-eee-ggg");
        model.setType("inspect");
        model.setAction("UPDATE");
        return model;
    }
}
