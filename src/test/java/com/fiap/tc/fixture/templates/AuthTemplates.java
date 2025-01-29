package com.fiap.tc.fixture.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fiap.tc.infrastructure.dto.AuthDetail;

public class AuthTemplates implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(AuthDetail.class).addTemplate("valid", new Rule() {
            {
                add("accessToken", "token-test");
                add("tokenType", "bearer");
                add("profile", random("SYSTEM_READ"));
                add("expiresIn", 86400);
            }
        });
    }

}
