package org.daly.VendingMachine.service;

import org.daly.VendingMachine.model.Coin;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class CoinListMatcher {

    public static Matcher<List<Coin>> containsCoin(Coin expectedCoin) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(List<Coin> actualCoins) {
                return actualCoins.stream().anyMatch(actualCoin -> actualCoin.equals(expectedCoin));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" contains coin ").appendValue(expectedCoin);
            }
        };
    }
}
