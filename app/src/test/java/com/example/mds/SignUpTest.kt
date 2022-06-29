package com.example.mds

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */



class SignUpTest {
    /*
    test case for empty username
    */
    @Test
    fun `empty username address returns false`() {
        val resultValue = SignUpValidation.isValid(
            "",
            "test@gmail.com",
            "Test1234567"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for empty password
    */
    @Test
    fun `empty password returns false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "test@gmail.com",
            ""
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for empty email
    */
    @Test
    fun `empty email returns false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "",
            "Test1234567"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for empty email address and password
    */
    @Test
    fun `empty email address and password returns false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "",
            ""
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for valid email and invalid password (less than 8 characters)
    */
    @Test
    fun `valid email address and password is less than 6 characters returns false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "test@gmail.com",
            "Test12"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for valid email and invalid password (no upper letter)
    */
    @Test
    fun `valid email address and password with no upper letter false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "test@gmail.com",
            "test1234567"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for valid email and invalid password (< 3 digits)
    */
    @Test
    fun `valid email address and password with less than 3 digits false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "test@gmail.com",
            "Test12"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for invalid email and valid password
    */
    @Test
    fun `invalid email address and valid password returns false`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "email",
            "Test1234567"
        )
        assertEquals(false, resultValue)
    }

    /*
    test case for all valid fields
    */
    @Test
    fun `valid email address and password returns true`() {
        val resultValue = SignUpValidation.isValid(
            "user1",
            "test@gmail.com",
            "Test1234567"
        )
        assertEquals(true, resultValue)
    }
}