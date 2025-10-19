package io.github.patrickvillarroel.wheel.vault.domain.model

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeEmpty
import org.junit.Before
import org.junit.Test

class CarItemBuilderTest {
    private lateinit var carItemPartial: CarItem.Builder

    @Before
    fun setUp() {
        carItemPartial = CarItem.Builder(
            model = "Golf",
            year = 2014,
            manufacturer = "Hot Wheels",
            quantity = 1,
            brand = "Volkswagen",
            isFavorite = true,
            availableForTrade = false,
            description = "Nuevo",
            category = "Europe Icons",
        )
    }

    /** Given all data to create a car, when [CarItem.Builder.build] is called, then a [CarItem] is returned */
    @Test
    fun testCompleteDataSuccess() {
        val carItem = carItemPartial.build().shouldNotBeNull()

        carItem.model.shouldNotBeEmpty()
        carItem.model.shouldBeEqual("Golf")

        carItem.year.shouldBeEqual(2014)

        carItem.manufacturer.shouldNotBeEmpty()
        carItem.manufacturer.shouldBeEqual("Hot Wheels")

        carItem.quantity.shouldBeEqual(1)

        carItem.brand.shouldNotBeEmpty()
        carItem.brand.shouldBeEqual("Volkswagen")

        carItem.isFavorite.shouldNotBeNull().shouldBeEqual(true)

        carItem.availableForTrade.shouldNotBeNull().shouldBeEqual(false)

        carItem.description.shouldNotBeNull().shouldBeEqual("Nuevo")

        carItem.category.shouldNotBeNull().shouldBeEqual("Europe Icons")

        carItem.images.shouldContainExactly(CarItem.EmptyImage)
    }

    @Test
    fun testIncompleteDataReturnNull() {
        carItemPartial = CarItem.Builder(
            model = null,
            year = 2014,
            manufacturer = "Hot Wheels",
            quantity = -1,
            brand = null,
            isFavorite = false,
            availableForTrade = false,
            description = "xd",
            category = null,
        )
        carItemPartial.build().shouldBeNull()
    }

    @Test
    fun testUpdateCarItem() {
        // Create a car item
        val carItem = carItemPartial.build().shouldNotBeNull()
        // assume is saved and going to edit again

        // Convert to builder to update
        var carItemBuilder = carItem.toBuilder()
        // When going to update, the id is already set
        carItemBuilder.id.shouldNotBeNull()

        // New values
        carItemBuilder = carItemBuilder.copy(
            model = "Golf",
            year = 2014,
            manufacturer = "HotWheels",
            quantity = 1,
            brand = null,
            images = setOf(),
            isFavorite = false,
            availableForTrade = false,
            description = null,
            category = null,
        ).removeEmptyProperties()

        // Convert back to car item and "save"
        val updatedCarItem = carItemBuilder.build().shouldNotBeNull()

        // Check values updated
        updatedCarItem.model.shouldBeEqual("Golf")
        updatedCarItem.year.shouldBeEqual(2014)
        updatedCarItem.manufacturer.shouldBeEqual("HotWheels")
        updatedCarItem.quantity.shouldBeEqual(1)
        updatedCarItem.brand.shouldBeEqual("HotWheels")
        updatedCarItem.images.shouldContainExactly(CarItem.EmptyImage)
        updatedCarItem.isFavorite.shouldBeEqual(false)
        updatedCarItem.availableForTrade.shouldBeEqual(false)
        updatedCarItem.description.shouldBeNull()
        updatedCarItem.category.shouldBeNull()
    }
}
