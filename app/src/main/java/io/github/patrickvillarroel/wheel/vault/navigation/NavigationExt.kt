/** This file contain a extension only callable in [EntryProviderBuilder] to convert a domain model to navigation model */
package io.github.patrickvillarroel.wheel.vault.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/** Extension function to convert a [CarItem] to a [NavigationKeys.CarEdit], only callable in a [EntryProviderBuilder] */
context(_: EntryProviderBuilder<NavKey>)
fun CarItem.toCarEdit(): NavigationKeys.CarEdit = this.toPartial().toCarEdit()

/** Extension function to convert a [CarItem.Partial] to a [NavigationKeys.CarEdit], only callable in a [EntryProviderBuilder] */
@OptIn(ExperimentalUuidApi::class)
context(_: EntryProviderBuilder<NavKey>)
fun CarItem.Partial.toCarEdit(): NavigationKeys.CarEdit {
    val partial = this
    return NavigationKeys.CarEdit(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        // TODO this convert is unsafe
        images = partial.images.map { it.toString() }.toSet(),
        description = partial.description,
        category = partial.category,
        id = partial.id?.toKotlinUuid(),
    )
}

/** Extension function to convert a [NavigationKeys.CarEdit] to [CarItem.Partial], especial only callable in a [EntryProviderBuilder] */
@OptIn(ExperimentalUuidApi::class)
context(_: EntryProviderBuilder<NavKey>)
fun NavigationKeys.CarEdit.toCarPartial(): CarItem.Partial {
    val partial = this
    return CarItem.Partial(
        model = partial.model,
        brand = partial.brand,
        year = partial.year,
        quantity = partial.quantity,
        manufacturer = partial.manufacturer,
        isFavorite = partial.isFavorite,
        images = partial.images,
        description = partial.description,
        category = partial.category,
        id = partial.id?.toJavaUuid(),
    )
}

/**
 * Add entry provider to [EntryProviderBuilder].
 *
 * @param T the type of the key for this NavEntry
 * @param transitionSpec the transition spec for this entry. See [NavDisplay.transitionSpec].
 * @param popTransitionSpec the transition spec when popping this entry from backstack.
 * See [NavDisplay.popTransitionSpec].
 * @param predictivePopTransitionSpec the transition spec when popping this entry from backstack using the predictive back gesture.
 * See [NavDisplay.predictivePopTransitionSpec].
 * @param metadata provides information to the display
 * @param content content for this entry to be displayed when this entry is active with [AnimatedContentScope] of [LocalNavAnimatedContentScope].
 */
inline fun <reified T : NavKey> EntryProviderBuilder<*>.entry(
    noinline transitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline popTransitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    noinline predictivePopTransitionSpec: (AnimatedContentTransitionScope<*>.() -> ContentTransform?)? = null,
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(T) -> Unit,
) {
    val metadata = buildMap {
        putAll(metadata)
        transitionSpec?.let { putAll(NavDisplay.transitionSpec(transitionSpec)) }
        popTransitionSpec?.let { putAll(NavDisplay.popTransitionSpec(popTransitionSpec)) }
        predictivePopTransitionSpec?.let { putAll(NavDisplay.predictivePopTransitionSpec(predictivePopTransitionSpec)) }
    }

    this.entry<T>(
        metadata = metadata,
        content = { entry ->
            with(LocalNavAnimatedContentScope.current) {
                content(entry)
            }
        },
    )
}
