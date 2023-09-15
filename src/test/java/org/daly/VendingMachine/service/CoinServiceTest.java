package org.daly.VendingMachine.service;

import org.daly.VendingMachine.controller.InsufficientChangeException;
import org.daly.VendingMachine.model.Coin;
import org.daly.VendingMachine.model.CoinType;
import org.daly.VendingMachine.repository.CoinRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.daly.VendingMachine.service.CoinListMatcher.containsCoin;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoinServiceTest {
    @InjectMocks
    private CoinService coinService;
    @Mock
    private CoinRepository coinRepository;

    @Nested
    class CalculateChange {
        @Test
        public void shouldReturnEmptyListIfNoChangeIsRequired() {
            // Give a payment for the full purchase price
            int payment = 65;
            int purchasePrice = 65;

            // When getting the change
            List<Coin> result = coinService.calculateChange(payment, purchasePrice);

            // Then an empty collection of coins should be returned
            assertThat("An empty collection of coins is returned", result, is(emptyCollectionOf(Coin.class)));
        }

        @Test
        public void shouldThrowExceptionIfExactChangeCannotBeGiven() {
            // Give a payment for more than the payment price for which exact change cannot be given
            int payment = 67;
            int purchasePrice = 65;

            // When getting the change
            // Then an exception should be thrown
            assertThrows(InsufficientChangeException.class, () -> coinService.calculateChange(payment, purchasePrice));
        }

        @Nested
        class PoundCoins {
            @Test
            public void shouldReturnListContainingPoundsIfPaymentIsFullPoundsMoreThanPurchasePrice() {
                // Give a payment for the two pounds more than purchase price
                int payment = 265;
                int purchasePrice = 65;
                Coin poundCoins = new Coin(CoinType.POUND, 5);
                doReturn(List.of(poundCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then two pound coins should be returned as change
                assertThat("A collection containing two pound coins is returned", result, containsCoin(new Coin(CoinType.POUND, 2)));
            }

            @Test
            public void shouldReturnMaxNumberOfPoundCoinsFromRepositoryIfNotEnoughToCoverFullChangeRequirement() {
                // Give a payment for the full purchase price and one pound coin in storage
                int payment = 265;
                int purchasePrice = 65;
                Coin poundCoins = new Coin(CoinType.POUND, 1);
                Coin fiftyCoins = new Coin(CoinType.FIFTY_PENCE, 5);
                doReturn(List.of(poundCoins, fiftyCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then one pound coin should be returned in the change
                assertThat("A collection containing two pound coins is returned", result, containsCoin(new Coin(CoinType.POUND, 1)));
            }
        }

        @Nested
        class FiftyCoins {

            @Test
            public void shouldReturnListContainingFiftyPenceIfPaymentIsMoreThanPurchasePrice() {
                // Give a payment for one pound more than the purchase price and one fifty pence in the repository and no pound coins available
                int payment = 165;
                int purchasePrice = 65;
                Coin fiftyCoins = new Coin(CoinType.FIFTY_PENCE, 5);
                doReturn(List.of(fiftyCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then two fifty pence coins should be returned as change
                assertThat("A collection containing two fifty pence coins is returned", result, containsCoin(new Coin(CoinType.FIFTY_PENCE, 2)));
            }

            @Test
            public void shouldReturnMaxNumberOfFiftyCoinsFromRepositoryIfNotEnoughToCoverFullChangeRequirement() {
                // Give a payment for one pound more than the purchase price and one fifty pence in the repository and no pound coins available
                int payment = 165;
                int purchasePrice = 65;
                Coin fiftyCoins = new Coin(CoinType.FIFTY_PENCE, 1);
                Coin twentyCoins = new Coin(CoinType.TWENTY_PENCE, 5);
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 5);
                doReturn(List.of(fiftyCoins, twentyCoins, tenCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a fifty pence coin should be returned as change
                assertThat("A collection containing a fifty pence coin is returned", result, containsCoin(new Coin(CoinType.FIFTY_PENCE, 1)));
            }

            @Test
            public void shouldReturnChangeContainingBothPoundCoinsAnfFiftyCoins() {
                // Give a payment for two pounds more than the purchase price and one pound coin in the repository and fifty pence available
                int payment = 265;
                int purchasePrice = 65;
                Coin poundCoins = new Coin(CoinType.POUND, 1);
                Coin fiftyCoins = new Coin(CoinType.FIFTY_PENCE, 5);
                doReturn(List.of(poundCoins, fiftyCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a fifty pence coin should be returned as change
                assertThat("A collection containing a fifty pence coin is returned", result, allOf(containsCoin(new Coin(CoinType.POUND, 1)), containsCoin(new Coin(CoinType.FIFTY_PENCE, 2))));
            }
        }

        @Nested
        class TwentyCoins {

            @Test
            public void shouldReturnListContainingTwentyPenceIfPaymentIsMoreThanPurchasePrice() {
                // Given a payment for forty pence more than the purchase price
                int payment = 105;
                int purchasePrice = 65;
                Coin twentyCoins = new Coin(CoinType.TWENTY_PENCE, 5);
                doReturn(List.of(twentyCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then two twenty pence coins should be returned as change
                assertThat("A collection containing two twenty pence coins is returned", result, containsCoin(new Coin(CoinType.TWENTY_PENCE, 2)));
            }

            @Test
            public void shouldReturnMaxNumberOfTwentyCoinsFromRepositoryIfNotEnoughToCoverFullChangeRequirement() {
                // Given a payment for forty pence more than the purchase price and one twenty pence in the repository
                int payment = 105;
                int purchasePrice = 65;
                Coin twentyCoins = new Coin(CoinType.TWENTY_PENCE, 1);
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 2);
                doReturn(List.of(twentyCoins, tenCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a twenty pence coin should be returned as change
                assertThat("A collection containing a twenty pence coin is returned", result, containsCoin(new Coin(CoinType.TWENTY_PENCE, 1)));
            }

            @Test
            public void shouldReturnChangeContainingBothFiftyCoinsAndTwentyCoins() {
                // Give a payment for 70 pence more than the purchase price and coins available
                int payment = 135;
                int purchasePrice = 65;
                Coin fiftyCoins = new Coin(CoinType.FIFTY_PENCE, 1);
                Coin twentyCoins = new Coin(CoinType.TWENTY_PENCE, 5);
                doReturn(List.of(fiftyCoins, twentyCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a fifty pence coin and twenty pence coin should be returned as change
                assertThat("A collection containing a fifty pence coin and twenty pence coin is returned", result, allOf(containsCoin(new Coin(CoinType.FIFTY_PENCE, 1)), containsCoin(new Coin(CoinType.TWENTY_PENCE, 1))));
            }
        }

        @Nested
        class TenCoins {

            @Test
            public void shouldReturnListContainingTenPenceIfPaymentIsMoreThanPurchasePrice() {
                // Given a payment for twenty pence more than the purchase price
                int payment = 85;
                int purchasePrice = 65;
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 5);
                doReturn(List.of(tenCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then two ten pence coins should be returned as change
                assertThat("A collection containing two ten pence coins is returned", result, containsCoin(new Coin(CoinType.TEN_PENCE, 2)));
            }

            @Test
            public void shouldReturnMaxNumberOfTenCoinsFromRepositoryIfNotEnoughToCoverFullChangeRequirement() {
                // Given a payment for twenty pence more than the purchase price and one ten pence in the repository
                int payment = 85;
                int purchasePrice = 65;
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 1);
                Coin fiveCoins = new Coin(CoinType.FIVE_PENCE, 2);
                doReturn(List.of(tenCoins, fiveCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a tenCoins pence coin should be returned as change
                assertThat("A collection containing a ten pence coin is returned", result, containsCoin(new Coin(CoinType.TEN_PENCE, 1)));
            }

            @Test
            public void shouldReturnChangeContainingBothTwentyCoinsAndTenCoins() {
                // Give a payment for 40 pence more than the purchase price and coins available
                int payment = 105;
                int purchasePrice = 65;
                Coin twentyCoins = new Coin(CoinType.TWENTY_PENCE, 1);
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 5);
                doReturn(List.of(twentyCoins, tenCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a twenty pence coin and two ten pence coin should be returned as change
                assertThat("A collection containing a twenty pence coin and two ten pence coin is returned", result, allOf(containsCoin(new Coin(CoinType.TWENTY_PENCE, 1)), containsCoin(new Coin(CoinType.TEN_PENCE, 2))));
            }
        }

        @Nested
        class FiveCoins {

            @Test
            public void shouldReturnListContainingTenPenceIfPaymentIsMoreThanPurchasePrice() {
                // Given a payment for ten pence more than the purchase price
                int payment = 75;
                int purchasePrice = 65;
                Coin fiveCoins = new Coin(CoinType.FIVE_PENCE, 5);
                doReturn(List.of(fiveCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then two five pence coins should be returned as change
                assertThat("A collection containing two ten pence coins is returned", result, containsCoin(new Coin(CoinType.FIVE_PENCE, 2)));
            }

            @Test
            public void shouldThrowExceptionIfCannotFulfilRequiredFivePence() {
                // Given a payment for ten pence more than the purchase price and one five pence in the repository
                int payment = 75;
                int purchasePrice = 65;
                Coin fiveCoins = new Coin(CoinType.FIVE_PENCE, 1);
                doReturn(List.of(fiveCoins)).when(coinRepository).findAll();

                // When getting the change
                // Then an exception should be thrown
                assertThrows(InsufficientChangeException.class, () -> coinService.calculateChange(payment, purchasePrice));
            }

            @Test
            public void shouldReturnChangeContainingBothTenCoinsAndFiveCoins() {
                // Give a payment for 20 pence more than the purchase price and coins available
                int payment = 85;
                int purchasePrice = 65;
                Coin tenCoins = new Coin(CoinType.TEN_PENCE, 1);
                Coin fiveCoins = new Coin(CoinType.FIVE_PENCE, 5);
                doReturn(List.of(tenCoins, fiveCoins)).when(coinRepository).findAll();

                // When getting the change
                List<Coin> result = coinService.calculateChange(payment, purchasePrice);

                // Then a ten pence coin and two five pence coin should be returned as change
                assertThat("A collection containing a ten pence coin and two five pence coin is returned", result, allOf(containsCoin(new Coin(CoinType.TEN_PENCE, 1)), containsCoin(new Coin(CoinType.FIVE_PENCE, 2))));
            }
        }
    }

    @Nested
    class MakePayment {
        @Captor
        ArgumentCaptor<List<Coin>> coinListCaptor;

        @Nested
        class AddCoins {
            @Test
            public void shouldAddPoundCoinsToRepository() {
                // Given a payment containing pound coins, no change and pound coins being stored
                List<Coin> payment = List.of(new Coin(CoinType.POUND, 1));
                List<Coin> available = List.of(new Coin(CoinType.POUND, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(payment, List.of());

                // Then the pound coins should be added to the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated pound coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.POUND, 2)));
            }

            @Test
            public void shouldAddFiftyCoinsToRepository() {
                // Given a payment containing fifty coins, no change and fifty coins being stored
                List<Coin> payment = List.of(new Coin(CoinType.FIFTY_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.FIFTY_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(payment, List.of());

                // Then the fifty coins should be added to the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated pound coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.FIFTY_PENCE, 2)));
            }

            @Test
            public void shouldAddTwentyCoinsToRepository() {
                // Given a payment containing twenty coins, no change and twenty coins being stored
                List<Coin> payment = List.of(new Coin(CoinType.TWENTY_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.TWENTY_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(payment, List.of());

                // Then the twenty coins should be added to the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated twenty coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.TWENTY_PENCE, 2)));
            }

            @Test
            public void shouldAddTenCoinsToRepository() {
                // Given a payment containing ten coins, no change and ten coins being stored
                List<Coin> payment = List.of(new Coin(CoinType.TEN_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.TEN_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(payment, List.of());

                // Then the ten coins should be added to the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated ten coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.TEN_PENCE, 2)));
            }

            @Test
            public void shouldAddFiveCoinsToRepository() {
                // Given a payment containing five coins, no change and five coins being stored
                List<Coin> payment = List.of(new Coin(CoinType.FIVE_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.FIVE_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(payment, List.of());

                // Then the five coins should be added to the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated five coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.FIVE_PENCE, 2)));
            }
        }

        @Nested
        class RemoveCoins {
            @Test
            public void shouldRemovePoundCoinsFromRepository() {
                // Given change containing pound coins, no payment and pound coins being stored
                List<Coin> change = List.of(new Coin(CoinType.POUND, 1));
                List<Coin> available = List.of(new Coin(CoinType.POUND, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(List.of(), change);

                // Then the pound coins should be removed from the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated pound coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.POUND, 0)));
            }

            @Test
            public void shouldRemoveFiftyCoinsFromRepository() {
                // Given change containing fifty coins, no payment and fifty coins being stored
                List<Coin> change = List.of(new Coin(CoinType.FIFTY_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.FIFTY_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(List.of(), change);

                // Then the fifty coins should be removed from the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated pound coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.FIFTY_PENCE, 0)));
            }

            @Test
            public void shouldRemoveTwentyCoinsFromRepository() {
                // Given change containing twenty coins, no payment and twenty coins being stored
                List<Coin> change = List.of(new Coin(CoinType.TWENTY_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.TWENTY_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(List.of(), change);

                // Then the twenty coins should be removed from the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated twenty coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.TWENTY_PENCE, 0)));
            }

            @Test
            public void shouldRemoveTenCoinsFromRepository() {
                // Given change containing ten coins, no payment and ten coins being stored
                List<Coin> change = List.of(new Coin(CoinType.TEN_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.TEN_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(List.of(), change);

                // Then the ten coins should be removed from the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated ten coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.TEN_PENCE, 0)));
            }

            @Test
            public void shouldRemoveFiveCoinsFromRepository() {
                // Given change containing five coins, no payment and five coins being stored
                List<Coin> change = List.of(new Coin(CoinType.FIVE_PENCE, 1));
                List<Coin> available = List.of(new Coin(CoinType.FIVE_PENCE, 1));
                doReturn(available).when(coinRepository).findAll();

                // When making the payment
                coinService.makePayment(List.of(), change);

                // Then the five coins should be removed from the repository
                verify(coinRepository).saveAll(coinListCaptor.capture());
                assertThat("Updated five coins are saved", coinListCaptor.getValue(), containsCoin(new Coin(CoinType.FIVE_PENCE, 0)));
            }
        }
    }
}