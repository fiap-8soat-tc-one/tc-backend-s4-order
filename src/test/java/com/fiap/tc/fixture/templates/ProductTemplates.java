package com.fiap.tc.fixture.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fiap.tc.domain.entities.Product;
import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.infrastructure.persistence.entities.embeddable.Audit;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTemplates implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(Product.class).addTemplate("valid", new Rule() {
            {
                add("id", UUID.randomUUID());
                add("idCategory", UUID.randomUUID());
                add("name", random("hamburger", "beer", "flan"));
                add("description", random("hamburger desc", "beer desc", "flan desc"));
                add("price", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
            }
        });


    }
}
