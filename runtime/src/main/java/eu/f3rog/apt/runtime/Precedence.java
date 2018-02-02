package eu.f3rog.apt.runtime;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

/**
 * 定义多个@Aspect的顺序
 *
 * @author Administrator
 * @date 2018/2/2 18:55
 */
@Aspect
@DeclarePrecedence(
        "eu.f3rog.apt.runtime.ThreadAspectConfig, eu.f3rog.apt.runtime.LoggedAspectConfig")
class Precedence {

}