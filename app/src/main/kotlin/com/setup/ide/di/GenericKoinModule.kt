package com.setup.ide.di

import org.koin.core.KoinApplication
import org.koin.core.definition.Kind.Factory
import org.koin.core.definition.Kind.Scoped
import org.koin.core.definition.Kind.Singleton
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * [GenericKoinModule] enables the use of generics with Koin.
 *
 * The implementation leans heavily on the Guice (TypeLiteral)
 * [https://github.com/google/guice/blob/master/core/src/com/google/inject/TypeLiteral.java] trick of
 * subclasses a genericized abstract class and reading the JVM reflection information at runtime. See
 * [GenericTypeQualifier] for limitations.
 *
 * This module structure is intentionally not as feature rich as the Koin modules, and should only be
 * extended if there is a clear benefit to the application design.
 *
 * Of note, there are no property methods as property injection is less clean that constructor for
 * basic usage. Nor does it provide multiple [Scope] access, parameters, etc. -- mostly on the YAGNI
 * principal.
 *
 * Special note: `getOrNull` is a horrible idea for use in [Module] (optional
 * dependencies? Please rethink the object lifecycle -- otherwise, the bugs are on you.)
 * So don't even think about it.
 *
 * Usage:
 * ```
 * val module = genericModule {
 *   single<Configuration<Jdk, GlobalScope>> { JdkConfiguration(get(), get(), get()) }
 * }
 *
 * val application = genericKoinApp {
 *    modules(module)
 * }
 *
 * ...
 *
 * // this usage of 'OrNull' is fine, as it's not part of the wiring.
 * application.getGenericOrNull<Configuration<Jdk, GlobalScope>>>()
 *
 * ```
 */
class GenericKoinModule(@PublishedApi internal val module: Module) {

  /**
   * [bind] associates additional types with an instance definition.
   *
   * This is predominately a convenience function that delegates back to [GenericKoinModule] --
   * allowing less boilerplate like any good DSL. The public [Module] api is too limited to allow
   * a deeper integration.
   */
  inline fun <reified T : Any> Pair<GenericKoinModule, InstanceFactory<*>>.bind(
    qualifier: Qualifier? = null
  ) =
    apply {
      val (_, factory) = this
      when (factory.beanDefinition.kind) {
        Singleton -> single<T>(qualifier) { parameters ->
          koinScope.get(
            factory.beanDefinition.primaryType,
            factory.beanDefinition.qualifier
          ) { parameters }
        }
        Factory -> factory<T>(qualifier) { parameters ->
          koinScope.get(
            factory.beanDefinition.primaryType,
            factory.beanDefinition.qualifier
          ) { parameters }
        }
        Scoped -> TODO("Scopes not currently supported.")
      }
    }

  inline fun <reified T : Any> single(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: GenericScope.(ParametersHolder) -> T
  ): Pair<GenericKoinModule, InstanceFactory<T>> {
    return module.single(
      qualifier = newGenericTypeQualifierOf<T>(qualifier),
      createdAtStart = createdAtStart,
      definition = {
        GenericScope(this).definition(it)
      }
    ).let { (_, factory) ->
      this to factory
    }
  }

  inline fun <reified T : Any> factory(
    qualifier: Qualifier? = null,
    noinline definition: GenericScope.(ParametersHolder) -> T
  ): Pair<GenericKoinModule, InstanceFactory<T>> {
    return module.factory(
      qualifier = newGenericTypeQualifierOf<T>(qualifier),
      definition = {
        GenericScope(this).definition(it)
      }
    ).let { (_, factory) -> this to factory }
  }

  class GenericKoinApplication(
    @PublishedApi
    internal val koinApplication: KoinApplication
  ) {

    /**
     * [getConcreteOrNull] retrieves non-genericized instances.
     *
     * Attempting to retrieve a class or interface with generics will always return null.
     */
    fun <T : Any> getConcreteOrNull(
      kClass: KClass<T>,
      qualifier: Qualifier? = null
    ): T? {
      return koinApplication.koin.getOrNull<T>(
        kClass,
        qualifier
      )
    }

    inline fun <reified T : Any> getGenericOrNull(
      kClass: KClass<T>,
      qualifier: Qualifier? = null
    ) = koinApplication.koin.getOrNull<T>(
      kClass,
      qualifier = newGenericTypeQualifierOf<T>(qualifier)
    )

    inline fun <reified T : Any> getGenericOrNull(qualifier: Qualifier? = null) =
      getGenericOrNull(T::class, qualifier)
  }

  companion object {

    /** [genericKoinApplication] returns a [GenericKoinApplication] to access the object graph. */
    fun genericKoinApplication(definition: KoinApplication.() -> Unit) = GenericKoinApplication(
      koinApplication { apply(definition) }
    )

    /** [genericModule] provides a Koin [Module] with jvm generic enhancements. */
    fun genericModule(
      createdAtStart: Boolean = false,
      definition: GenericKoinModule.() -> Unit
    ): Module = module(createdAtStart) {
      GenericKoinModule(this).apply(definition)
    }

    /** [GenericScope] defines a subset of the public [Scope] API */
    class GenericScope(@PublishedApi internal val koinScope: Scope) {
      // picked this gem out of kotlin.jvm.JvmClassMapping.
      inline fun <reified T : Any> get(
        kClass: KClass<T>,
        qualifier: Qualifier? = null
      ): T = koinScope.get(kClass, newGenericTypeQualifierOf<T>(qualifier))

      inline fun <reified T : Any> get(qualifier: Qualifier? = null): T =
        get(T::class, qualifier)
    }

    /**
     * [newGenericTypeQualifierOf] a [KClass] creates a generic aware qualifier.
     *
     * @param qualifier Optional, additional qualifier.
     */
    inline fun <reified T : Any> newGenericTypeQualifierOf(
      qualifier: Qualifier? = null
    ): Qualifier? {
      // it would be nice to cache this...
      val type = object : GenericTypeQualifier<T>() {}
      if (!type.hasGenerics) {
        return qualifier
      }
      return qualifier
        ?.let { CompositeQualifier(it, type) }
        ?: type
    }

    /**
     * [CompositeQualifier] combines multiple qualifiers into a single one.
     */
    class CompositeQualifier(private vararg val qualifiers: Qualifier) : Qualifier {
      override val value by lazy {
        qualifiers.joinToString("|") { it.value }.intern() // for memory and courtesy
      }
    }

    operator fun Qualifier?.plus(that: Qualifier?): Qualifier? = when (null) {
      this -> that
      that -> this
      else -> CompositeQualifier(this, that)
    }

    /**
     * [GenericTypeQualifier] embeds the generic information into a subclass for JVM reflection.
     *
     * To work, this class _must_ be subclassed with the intended type. E.g. either directly or inline.
     * It will not work if it is subclassed inside a genericized function:
     *
     * Works:
     * ```
     * val qualifier = object: GenericTypeQualifier<List<String>>() {}
     *
     * inline fun <reified T: Any> qualifier() = object: GenericTypeQualifier<T>() {}
     * ```
     * ```
     * Doesn't Work:
     * ```
     * fun <T: Any> qualifier() = object: GenericTypeQualifier<T>() {}
     * ```
     */
    @PublishedApi
    internal abstract class GenericTypeQualifier<T : Any> : Qualifier {

      private val parameterizedType by lazy {
        (this::class.java.genericSuperclass as ParameterizedType)
      }

      val hasGenerics by lazy {
        parameterizedType.actualTypeArguments[0] is ParameterizedType
      }

      override val value by lazy {
        // this could be more efficient by canonizing the type names.
        parameterizedType.actualTypeArguments.joinToString(
          ", "
        ) {
          it.typeName
        }.intern() // for memory and courtesy
      }

      override fun toString(): String {
        return value
      }
    }
  }
}
