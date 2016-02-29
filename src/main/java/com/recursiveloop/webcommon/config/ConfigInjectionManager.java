package com.recursiveloop.webcommon.config;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.Serializable;


@ApplicationScoped
public class ConfigInjectionManager implements Serializable {
  private static final String PARAM_NOT_FOUND_MSG
    = "No definition found for mandatory configuration parameter '%s'";

  @Inject @Any
  Instance<ApplicationConfig> m_tmpSettings;

  // After sorting m_tmpSettings by precedence in descending order
  private List<ApplicationConfig> m_settings;

  @PostConstruct
  public void init() {
    m_settings = new ArrayList<ApplicationConfig>();

    for (ApplicationConfig settings : m_tmpSettings) {
      m_settings.add(settings);
    }

    m_settings.sort((ApplicationConfig a, ApplicationConfig b) -> b.precedence() - a.precedence());
  }

  @Produces @ConfigParam
  public String get(InjectionPoint ip) throws MissingConfigParamException {
    ConfigParam anno = ip.getAnnotated().getAnnotation(ConfigParam.class);
    assert(anno != null);

    String key = anno.key();
    String defaultValue = anno.defaultValue();
    boolean mandatory = anno.mandatory();

    String full = ip.getMember().getDeclaringClass().getName();
    String member = ip.getMember().getName();

    if (key.equals("[unassigned]")) {
      key = member;
    }

    for (ApplicationConfig settings : m_settings) {
      String conf = settings.get(full + "." + key);
      if (conf != null) {
        return conf;
      }

      String simple = ip.getMember().getDeclaringClass().getSimpleName();
      conf = settings.get(simple + "." + key);
      if (conf != null) {
        return conf;
      }

      conf = settings.get(key);
      if (conf != null) {
        return conf;
      }
    }

    if (!mandatory) {
      return defaultValue;
    }

    throw new MissingConfigParamException(String.format(PARAM_NOT_FOUND_MSG, key));
  }
}
