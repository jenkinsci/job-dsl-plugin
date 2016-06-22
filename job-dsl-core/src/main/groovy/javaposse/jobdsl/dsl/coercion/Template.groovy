package javaposse.jobdsl.dsl.coercion

/**
 * class to coerce templates from one job type to another
 * coercion helper classes need to be called javaposse.jobdsl.dsl.coercion.<from>.To
 * and inherit from {@link AbstractCoercer}
 *
 * hyphens need to be swallowed
 *
 * e.g. javaposse.jobdsl.dsl.coercion.project.MatrixProject
 * for the class to coerce project to matrix-project
 */
 class Template {

     final Node coerce(Node existing, String to) {

         String  toCamel = to.capitalize().replaceAll(/-\w/) { it[1].toUpperCase() }
         String  fromClean = existing.name().replaceAll(/-/) { '' }

         try {
             String coercer = "javaposse.jobdsl.dsl.coercion.${fromClean}.${toCamel}"
             AbstractCoercer instance = this.class.classLoader.loadClass(coercer)?.newInstance()
             instance.coerce(existing)
         } catch (ClassNotFoundException ex) {
             //there is no class to coerce from to to
             existing
         }
    }

}
