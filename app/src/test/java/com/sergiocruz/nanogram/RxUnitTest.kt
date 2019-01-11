package com.sergiocruz.nanogram

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RxUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    @Throws(InterruptedException::class)
    fun main() {
        val just: Observable<String> = Observable.just("long", "longer", "longest")

        just.flatMap { v ->
                performLongOperation(v)!!
                    .doOnNext { s -> println("processing item $s on thread " + Thread.currentThread().name) }
                    .subscribeOn(Schedulers.newThread())
            }
            .subscribe { length -> println("received item length $length on thread ${Thread.currentThread().name}") }

        Thread.sleep(10000)
    }

    /**
     * Returns length of each param wrapped into an Observable.
     */
    private fun performLongOperation(string: String): Observable<Int>? {
        Thread.sleep((Random.nextInt(3) * 1000).toLong())
        return Observable.just(string.length)
    }

}
