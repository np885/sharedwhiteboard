package controllers.common.security;

import controllers.common.mediatypes.JSONonlyAction;
import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see AuthenticationAction
 */
@With(AuthenticationAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
}
