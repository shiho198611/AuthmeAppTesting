package com.testing.githubusersdk.extension

import org.junit.Assert
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun getGithubUsersNextPageUrlTest() {
        val link = "<https://api.github.com/users?since=46>; rel=\"next\", <https://api.github.com/users{?since}>; rel=\"first\""

        val actual = link.parseNextUrl()
        val expect = "https://api.github.com/users?since=46"

        Assert.assertEquals(expect, actual)
    }

    @Test
    fun searchGithubUsersNextPageUrlTest() {
        val link = "<https://api.github.com/search/users?q=s&page=2>; rel=\"next\", <https://api.github.com/search/users?q=s&page=34>; rel=\"last\""

        val actual = link.parseNextUrl()
        val expect = "https://api.github.com/search/users?q=s&page=2"

        Assert.assertEquals(expect, actual)
    }

    @Test
    fun getGithubUsersNextPageUrlNoPatternTest() {
        val link = "<https://api.github.com/users?since=46>; rel=\"other\", <https://api.github.com/users{?since}>; rel=\"first\""

        val actual = link.parseNextUrl()

        Assert.assertNull(actual)
    }

    @Test
    fun getGithubUsersNextPageUrlNullTest() {
        val link = null

        val actual = link.parseNextUrl()

        Assert.assertNull(actual)
    }

}