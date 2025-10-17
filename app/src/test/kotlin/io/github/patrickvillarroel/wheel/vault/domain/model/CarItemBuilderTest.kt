package io.github.patrickvillarroel.wheel.vault.domain.model

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeEmpty
import kotlin.test.BeforeTest
import kotlin.test.Test

class CarItemBuilderTest {
    private lateinit var carItemPartial: CarItem.Partial

    @BeforeTest
    fun setUp() {
        carItemPartial = CarItem.Partial(
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

    /** Given all data to create a car, when [CarItem.Partial.toCarItem] is called, then a [CarItem] is returned */
    @Test
    fun testCompleteDataSuccess() {
        val carItem = carItemPartial.toCarItem().shouldNotBeNull()

        carItem.model.shouldNotBeEmpty()
        carItem.model.shouldBeEqual("Golf")

        carItem.year shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual(2014)
        }

        carItem.manufacturer.shouldNotBeEmpty()
        carItem.manufacturer.shouldBeEqual("Hot Wheels")

        carItem.quantity shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual(1)
        }

        carItem.brand.shouldNotBeEmpty()
        carItem.brand.shouldBeEqual("Volkswagen")

        carItem.isFavorite shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual(true)
        }

        carItem.availableForTrade shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual(false)
        }

        carItem.description.shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual("Nuevo")
        }

        carItem.category.shouldNotBeNull {
            shouldNotBeNull()
            shouldBeEqual("Europe Icons")
        }

        carItem.images.shouldNotBeEmpty()
    }

    @Test
    fun testIncompleteDataReturNull() {
        carItemPartial = CarItem.Partial(
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
        carItemPartial.toCarItem().shouldBeNull()
    }
}
