package it.csttech.dbloader.entities;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Sortable {
    public int index() ;
}
