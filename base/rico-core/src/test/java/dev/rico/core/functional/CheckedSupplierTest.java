package dev.rico.core.functional;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class CheckedSupplierTest {

    @Test
    public void testSuccess() {
        // when
        final Result<String> result = Optional.ofNullable((Result<String>) null).orElseGet(Result.of(() -> "Hello"));

        // then
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccessful());
        Assert.assertFalse(result.isFailed());

        Assert.assertEquals(result.getResult(), "Hello");
        Assert.assertNull(result.getException());
    }

    @Test
    public void testFail() {
        // when
        final Result<String> result = Optional.ofNullable((Result<String>) null).orElseGet(Result.of(() -> throwException()));

        // then
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccessful());
        Assert.assertTrue(result.isFailed());
        Assert.assertNotNull(result.getException());
        Assert.assertEquals(result.getException().getClass(), RuntimeException.class);
        Assert.assertEquals(result.getException().getMessage(), "ERROR");
        try {
            result.getResult();
            Assert.fail("A fail should not have an result");
        } catch (final Exception e) {
            Assert.assertEquals(e.getClass(), IllegalStateException.class);
        }
    }

    private <A> A throwException() {
        throw new RuntimeException("ERROR");
    }
}
