package com.fiap.tc.fixture.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fiap.tc.domain.entities.Customer;

import java.util.UUID;

public class CustomerTemplates implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(Customer.class).addTemplate("valid", new Rule() {
            {
                add("id", UUID.randomUUID());
                add("name", random("Myller", "Jean", "Caio"));
                add("document", random("52735617017", "03014336076", "90819176095"));
                add("email", random("myllersakaguchi@gmail.com", "test@test.com"));
            }
        });
    }

}
