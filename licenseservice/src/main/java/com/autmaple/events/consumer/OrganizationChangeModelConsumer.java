package com.autmaple.events.consumer;

import com.autmaple.events.model.OrganizationChangeModel;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class OrganizationChangeModelConsumer implements Consumer<OrganizationChangeModel> {
    @Override
    public void accept(OrganizationChangeModel organizationChangeModel) {
        System.out.println(organizationChangeModel.toString());
    }
}
