package com.recursiveloop.webcommondemo;

import com.recursiveloop.webcommondemo.resources.*;
import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;


@ApplicationPath("rest")
public class RestApp extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(RcAccountImpl.class);
    s.add(RcAuthTokenImpl.class);
    s.add(RcPendingAccountImpl.class);
    s.add(RcRestrictedResourceImpl.class);
    s.add(RcUnrestrictedResourceImpl.class);

    return s;
  }
}
