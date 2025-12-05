package io.github.patrickvillarroel.wheel.vault.integration.trade

import io.github.patrickvillarroel.wheel.vault.data.datasource.supabase.TradeSupabaseDataSource
import io.github.patrickvillarroel.wheel.vault.domain.model.CarItem
import io.github.patrickvillarroel.wheel.vault.domain.model.TradeProposal
import io.github.patrickvillarroel.wheel.vault.domain.repository.CarsRepository
import io.github.patrickvillarroel.wheel.vault.domain.repository.TradeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.Uuid

/**
 * Integration test for Trade CRUD operations.
 * Tests the complete flow: UI -> ViewModel -> TradeRepository (mocked Supabase)
 *
 * Since trades only work with Supabase (no local Room storage),
 * we mock the TradeSupabaseDataSource to simulate server responses.
 */
class TradeCrudIntegrationTest : KoinTest {
    private val tradeRepository: TradeRepository by inject()
    private val carsRepository: CarsRepository by inject()

    // Get the mocked TradeSupabaseDataSource to configure behavior
    private val tradeSupabaseDataSource: TradeSupabaseDataSource by inject()

    private lateinit var car1: CarItem
    private lateinit var car2: CarItem
    private val userId1 = Uuid.random()
    private val userId2 = Uuid.random()

    @Before
    fun setup() = runTest {
        // Setup test data - two cars for trading
        car1 = CarItem(
            model = "Mustang GT",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
            availableForTrade = true,
        )

        car2 = CarItem(
            model = "Camaro ZL1",
            year = 2024,
            brand = "HotWheels",
            manufacturer = "HotWheels",
            quantity = 1,
            images = setOf(CarItem.EmptyImage),
            imageUrl = CarItem.EmptyImage,
            isFavorite = false,
            availableForTrade = true,
        )

        // Insert cars into repository for testing
        car1 = carsRepository.insert(car1)
        car2 = carsRepository.insert(car2)
    }

    @Test
    fun tradeCrud_createTradeProposal_callsSupabaseCorrectly() = runTest {
        // Given - Mock the trade proposal creation
        val tradeGroupId = Uuid.random()
        val expectedProposal = TradeProposal(
            id = Uuid.random(),
            tradeGroupId = tradeGroupId,
            requesterId = userId1,
            ownerId = userId2,
            offeredCarId = car1.id,
            requestedCarId = car2.id,
            eventType = TradeProposal.TradeEventType.PROPOSED,
            message = "I want to trade!",
            expiresAt = Clock.System.now() + 168.hours,
            createdAt = Clock.System.now(),
            createdBy = userId1,
        )

        coEvery {
            tradeSupabaseDataSource.createTradeProposal(
                offeredCarId = any(),
                requestedCarId = any(),
                message = any(),
                expirationHours = any(),
            )
        } returns expectedProposal

        // When - Create trade proposal directly through repository
        val result = tradeRepository.createTradeProposal(
            offeredCarId = car1.id,
            requestedCarId = car2.id,
            message = "I want to trade!",
            expirationHours = 168,
        )

        // Then - Verify the result and repository method was called
        assertNotNull(result)
        assertEquals(TradeProposal.TradeEventType.PROPOSED, result.eventType)
        assertEquals(car1.id, result.offeredCarId)
        assertEquals(car2.id, result.requestedCarId)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.createTradeProposal(
                offeredCarId = any(),
                requestedCarId = any(),
                message = any(),
                expirationHours = any(),
            )
        }
    }

    @Test
    fun tradeCrud_acceptTrade_updatesTradeStatus() = runTest {
        // Given - An existing trade proposal
        val tradeGroupId = Uuid.random()
        val acceptedProposal = TradeProposal(
            id = Uuid.random(),
            tradeGroupId = tradeGroupId,
            requesterId = userId1,
            ownerId = userId2,
            offeredCarId = car1.id,
            requestedCarId = car2.id,
            eventType = TradeProposal.TradeEventType.ACCEPTED,
            message = "Accepted!",
            createdAt = Clock.System.now(),
            createdBy = userId2,
        )

        coEvery {
            tradeSupabaseDataSource.respondToTrade(
                tradeGroupId = any(),
                accept = any(),
                responseMessage = any(),
            )
        } returns acceptedProposal

        // When - Accept the trade
        val result = tradeRepository.respondToTrade(
            tradeGroupId = tradeGroupId,
            accept = true,
            responseMessage = "Accepted!",
        )

        // Then - Verify the response
        assertNotNull(result)
        assertEquals(TradeProposal.TradeEventType.ACCEPTED, result.eventType)
        assertEquals("Accepted!", result.message)
        assertEquals(tradeGroupId, result.tradeGroupId)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.respondToTrade(
                tradeGroupId = any(),
                accept = any(),
                responseMessage = any(),
            )
        }
    }

    @Test
    fun tradeCrud_rejectTrade_updatesTradeStatus() = runTest {
        // Given - An existing trade proposal
        val tradeGroupId = Uuid.random()
        val rejectedProposal = TradeProposal(
            id = Uuid.random(),
            tradeGroupId = tradeGroupId,
            requesterId = userId1,
            ownerId = userId2,
            offeredCarId = car1.id,
            requestedCarId = car2.id,
            eventType = TradeProposal.TradeEventType.REJECTED,
            message = "Not interested",
            createdAt = Clock.System.now(),
            createdBy = userId2,
        )

        coEvery {
            tradeSupabaseDataSource.respondToTrade(
                tradeGroupId = any(),
                accept = any(),
                responseMessage = any(),
            )
        } returns rejectedProposal

        // When - Reject the trade
        val result = tradeRepository.respondToTrade(
            tradeGroupId = tradeGroupId,
            accept = false,
            responseMessage = "Not interested",
        )

        // Then - Verify the response
        assertNotNull(result)
        assertEquals(TradeProposal.TradeEventType.REJECTED, result.eventType)
        assertEquals("Not interested", result.message)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.respondToTrade(
                tradeGroupId = any(),
                accept = any(),
                responseMessage = any(),
            )
        }
    }

    @Test
    fun tradeCrud_cancelTrade_updatesTradeStatus() = runTest {
        // Given - An existing trade proposal
        val tradeGroupId = Uuid.random()
        val cancelledProposal = TradeProposal(
            id = Uuid.random(),
            tradeGroupId = tradeGroupId,
            requesterId = userId1,
            ownerId = userId2,
            offeredCarId = car1.id,
            requestedCarId = car2.id,
            eventType = TradeProposal.TradeEventType.CANCELLED,
            message = "Propuesta cancelada",
            createdAt = Clock.System.now(),
            createdBy = userId1,
        )

        coEvery {
            tradeSupabaseDataSource.cancelTrade(tradeGroupId = any())
        } returns cancelledProposal

        // When - Cancel the trade
        val result = tradeRepository.cancelTrade(tradeGroupId = tradeGroupId)

        // Then - Verify the response
        assertNotNull(result)
        assertEquals(TradeProposal.TradeEventType.CANCELLED, result.eventType)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.cancelTrade(tradeGroupId = any())
        }
    }

    @Test
    fun tradeCrud_getActiveTrades_returnsActiveProposals() = runTest {
        // Given - Mock active trades
        val now = Clock.System.now()
        val activeTrades = listOf(
            TradeProposal.CurrentTradeStatus(
                tradeGroupId = Uuid.random(),
                currentStatus = TradeProposal.TradeEventType.PROPOSED,
                effectiveStatus = "proposed",
                requesterId = userId1,
                ownerId = userId2,
                offeredCarId = car1.id,
                requestedCarId = car2.id,
                initialMessage = "Want to trade?",
                lastMessage = "Want to trade?",
                proposedAt = now,
                lastUpdated = now,
                originalExpiresAt = now + 168.hours,
                isActive = true,
                isSuccessful = false,
            ),
        )

        coEvery {
            tradeSupabaseDataSource.getActiveTrades()
        } returns activeTrades

        // When - Get active trades
        val result = tradeRepository.getActiveTrades()

        // Then - Verify the result
        assertEquals(1, result.size)
        assertTrue(result[0].isActive)
        assertEquals("proposed", result[0].effectiveStatus)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.getActiveTrades()
        }
    }

    @Test
    fun tradeCrud_getTradeHistory_returnsCompleteHistory() = runTest {
        // Given - Mock trade history with multiple events
        val tradeGroupId = Uuid.random()
        val now = Clock.System.now()

        val history = listOf(
            TradeProposal(
                id = Uuid.random(),
                tradeGroupId = tradeGroupId,
                requesterId = userId1,
                ownerId = userId2,
                offeredCarId = car1.id,
                requestedCarId = car2.id,
                eventType = TradeProposal.TradeEventType.PROPOSED,
                message = "Initial proposal",
                createdAt = now,
                createdBy = userId1,
            ),
            TradeProposal(
                id = Uuid.random(),
                tradeGroupId = tradeGroupId,
                requesterId = userId1,
                ownerId = userId2,
                offeredCarId = car1.id,
                requestedCarId = car2.id,
                eventType = TradeProposal.TradeEventType.ACCEPTED,
                message = "Accepted",
                createdAt = now + 1.hours,
                createdBy = userId2,
            ),
        )

        coEvery {
            tradeSupabaseDataSource.getTradeHistory(any())
        } returns history

        // When - Get trade history
        val result = tradeRepository.getTradeHistory(tradeGroupId.toString())

        // Then - Verify the complete history
        assertEquals(2, result.size)
        assertEquals(TradeProposal.TradeEventType.PROPOSED, result[0].eventType)
        assertEquals(TradeProposal.TradeEventType.ACCEPTED, result[1].eventType)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.getTradeHistory(any())
        }
    }

    @Test
    fun tradeCrud_getAvailableCarsForTrade_returnsOnlyAvailableCars() = runTest {
        // Given - Mock available cars
        val availableCars = listOf(car1, car2)

        coEvery {
            tradeSupabaseDataSource.getAvailableCarsForTrade()
        } returns availableCars

        // When - Get available cars
        val result = tradeRepository.getAvailableCarsForTrade()

        // Then - Verify that the method was called and results match mock
        assertEquals(2, result.size)
        assertEquals(car1.id, result[0].id)
        assertEquals(car2.id, result[1].id)
        // Ensure all cars are available for trade FIXME
        // assertTrue(result.all { it.availableForTrade })

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.getAvailableCarsForTrade()
        }
    }

    @Test
    fun tradeCrud_getActiveTradesForCar_returnsRelatedTrades() = runTest {
        // Given - Mock trades for a specific car
        val now = Clock.System.now()
        val tradesForCar = listOf(
            TradeProposal.CurrentTradeStatus(
                tradeGroupId = Uuid.random(),
                currentStatus = TradeProposal.TradeEventType.PROPOSED,
                effectiveStatus = "proposed",
                requesterId = userId1,
                ownerId = userId2,
                offeredCarId = car1.id,
                requestedCarId = car2.id,
                initialMessage = "Trade?",
                lastMessage = "Trade?",
                proposedAt = now,
                lastUpdated = now,
                originalExpiresAt = now + 168.hours,
                isActive = true,
                isSuccessful = false,
            ),
        )

        coEvery {
            tradeSupabaseDataSource.getActiveTradesForCar(any())
        } returns tradesForCar

        // When - Get trades for car
        val result = tradeRepository.getActiveTradesForCar(car1.id)

        // Then - Verify results
        assertEquals(1, result.size)
        assertEquals(car1.id, result[0].offeredCarId)

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.getActiveTradesForCar(any())
        }
    }

    @Test
    fun tradeCrud_getMyOfferedCarsInActiveTrades_returnsOfferedCarIds() = runTest {
        // Given - Mock offered car IDs
        val offeredCarIds = setOf(car1.id)

        coEvery {
            tradeSupabaseDataSource.getMyOfferedCarsInActiveTrades()
        } returns offeredCarIds

        // When - Get offered car IDs
        val result = tradeRepository.getMyOfferedCarsInActiveTrades()

        // Then - Verify results
        assertEquals(1, result.size)
        assertTrue(result.contains(car1.id))

        coVerify(atLeast = 1) {
            tradeSupabaseDataSource.getMyOfferedCarsInActiveTrades()
        }
    }
}
