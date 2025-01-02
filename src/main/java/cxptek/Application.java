package cxptek;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Application {

    public static void main(String[] args) {
       Injector injector = Guice.createInjector(new ApplicationModule());

    }
}
