package nablarch.core.beans

import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.*
import java.util.*

/**
 * [nablarch.core.beans.BeanUtil]にの[java.util.List]及び配列に関するテスト。
 */
internal class BeanUtilTestWithListAndArray {

    data class WithList(
        var name: String? = null,
        var strs: List<String>? = null,
        var nums: ArrayList<Int>? = null,
        var objects: LinkedList<Obj>? = null
    )

    data class WithArray(
        var name: String? = null,
        var strs: Array<String>? = null,
        var nums: Array<Int>? = null,
        var objects: Array<Obj>? = null
    )

    data class WithSet(
        var name: String? = null,
        var strs: Set<String>? = null,
        var nums: TreeSet<Int>? = null,
        var objects: Set<Obj>? = null
    )

    data class Obj(var name: String? = null)

    @Test
    fun createAndCopy_オブジェクトからListをコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.createAndCopy(WithList::class.java, src)
        assertThat("コピー元と同じ状態のオブジェクトが構築されること", actual, `is`(src))
    }

    @Test
    fun createAndCopy_オブジェクトからSetをコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.createAndCopy(WithSet::class.java, src)
        assertThat("コピー元と同じ状態のオブジェクトが構築されること", actual,
            `is`(
                WithSet(
                    "なまえ",
                    setOf("a", "1", "aaa"),
                    TreeSet(setOf(1, 10, 100)),
                    setOf(Obj("a"), Obj("aa"))
                )
            )
        )
    }

    @Test
    fun createAndCopy_オブジェクトから指定したプロパティのListがコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))


        val actual = BeanUtil.createAndCopyIncludes(WithList::class.java, src, "name", "strs", "objects")
        assertThat("指定しなかったnumsプロパティ以外がコピーされていること", actual,
            `is`(WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                null,
                LinkedList(listOf(Obj("a"), Obj("aa")))
            )))
    }

    @Test
    fun createAndCopy_オブジェクトから除外したプロパティ以外のListがコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))


        val actual = BeanUtil.createAndCopyExcludes(WithList::class.java, src, "objects")
        assertThat("指定しなかったnumsプロパティ以外がコピーされていること", actual,
            `is`(WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                arrayListOf(1, 10, 100),
                null
            )))
    }

    @Test
    fun createAndCopy_オブジェクトが持つ配列がListいコピーできること() {
        val actual = BeanUtil.createAndCopy(WithList::class.java,
            WithArray(
                "aaa",
                arrayOf("a", "b"),
                arrayOf(100, 200, 300),
                kotlin.emptyArray()
            )
        )

        assertThat(actual, `is`(WithList(
            "aaa",
            listOf("a", "b"),
            arrayListOf(100, 200, 300),
            LinkedList()
        )))
    }

    @Test
    fun createAndCopy_MapからListがコピーできること() {

        val actual = BeanUtil.createAndCopy(WithList::class.java,
            mapOf(
                "name" to "なまえ",
                "strs" to arrayOf("a", "1", "aaa"),
                "nums" to listOf(1, 11, 111),
                "objects" to listOf(Obj("あ"), Obj("い"))
            ))

        assertThat("指定しなかったnumsプロパティ以外がコピーされていること", actual,
            `is`(WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                arrayListOf(1, 11, 111),
                LinkedList(listOf(Obj("あ"), Obj("い")))
            )))
    }

    @Test
    fun createAndCopy_Mapから指定したプロパティのListがコピーできること() {

        val actual = BeanUtil.createAndCopyIncludes(WithList::class.java,
            mapOf(
                "name" to "なまえ",
                "strs" to arrayOf("a", "1", "aaa"),
                "nums" to listOf(1, 11, 111),
                "objects" to listOf(Obj("あ"), Obj("い"))
            ), "name", "nums")

        assertThat("指定しなかったnumsプロパティ以外がコピーされていること", actual,
            `is`(WithList(
                "なまえ",
                null,
                arrayListOf(1, 11, 111),
                null
            )))
    }

    @Test
    fun createAndCopy_Mapから除外したプロパティ以外のListがコピーできること() {

        val actual = BeanUtil.createAndCopyExcludes(WithList::class.java,
            mapOf(
                "name" to "なまえ",
                "strs" to arrayOf("a", "1", "aaa"),
                "nums" to listOf(1, 11, 111),
                "objects" to listOf(Obj("あ"), Obj("い"))
            ), "nums")

        assertThat("指定しなかったnumsプロパティ以外がコピーされていること", actual,
            `is`(WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                null,
                LinkedList(listOf(Obj("あ"), Obj("い")))
            )))
    }

    @Test
    fun copy_オブジェクトからListがコピーできること() {
        val src = WithSet(
            "なまえ",
            setOf("a", "1", "aaa"),
            TreeSet(setOf(1, 10, 100)),
            setOf(Obj("a"), Obj("aa")))

        val actual = BeanUtil.copy(src, WithList())
        assertThat(actual, `is`(
            WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                arrayListOf(1, 10, 100),
                LinkedList(listOf(Obj("a"), Obj("aa")))
            )
        ))
    }

    @Test
    fun copy_オブジェクトからSetがコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.copy(src, WithSet())
        assertThat(actual, `is`(WithSet(
            "なまえ",
            setOf("a", "1", "aaa"),
            TreeSet(setOf(1, 10, 100)),
            setOf(Obj("a"), Obj("aa")))
        ))
    }

    @Test
    fun copy_オブジェクトが持つListから配列にコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.copy(src, WithArray())
        assertThat(actual.name, `is`("なまえ"))
        assertThat(actual.strs, arrayContaining("a", "1", "aaa"))
        assertThat(actual.nums, arrayContaining(1, 10, 100))
        assertThat(actual.objects, arrayContaining(Obj("a"), Obj("aa")))
    }

    @Test
    fun copy_オブジェクトが持つ指定のプロパティのListがコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.copyIncludes(src, WithList(), "name", "nums", "strs")
        assertThat(actual, `is`(
            WithList(
                "なまえ",
                listOf("a", "1", "aaa"),
                arrayListOf(1, 10, 100),
                null
            )))
    }

    @Test
    fun copy_オブジェクトが持つ除外プロパティ以外のListがコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.copyExcludes(src, WithArray(), "name", "nums", "strs")
        assertThat(actual.name, `is`(nullValue()))
        assertThat(actual.strs, `is`(nullValue()))
        assertThat(actual.nums, `is`(nullValue()))
        assertThat(actual.objects, arrayContaining(Obj("a"), Obj("aa")))
    }

    @Test
    fun createMapAndCopy_ListがMapにコピーできること() {
        val src = WithList(
            "なまえ",
            listOf("a", "1", "aaa"),
            arrayListOf(1, 10, 100),
            LinkedList(listOf(Obj("a"), Obj("aa"))))

        val actual = BeanUtil.createMapAndCopy(src)
        assertThat(actual, `is`(
            mapOf(
                "name" to "なまえ",
                "strs" to listOf("a", "1", "aaa"),
                "nums" to arrayListOf(1, 10, 100),
                "objects" to LinkedList(listOf(Obj("a"), Obj("aa")))
            )
        ))
    }
    
    @Test
    fun createMapAndCopy_SetがMapにコピーできること() {
        val src = WithSet(
            "なまえ",
            setOf("a", "1", "aaa"),
            TreeSet(setOf(1, 10, 100)),
            setOf(Obj("a"), Obj("aa")))

        val actual = BeanUtil.createMapAndCopy(src)
        assertThat(actual, `is`(
            mapOf(
                "name" to "なまえ",
                "strs" to setOf("a", "1", "aaa"),
                "nums" to TreeSet(setOf(1, 10, 100)),
                "objects" to setOf(Obj("a"), Obj("aa"))
                )
            ))
    }
    
    @Test
    fun createMapAndCopy_配列がMapにコピーできること() {
        val src = WithArray(
            "なまえ",
            arrayOf("a", "1", "aaa"),
            arrayOf(1, 10, 100),
            arrayOf(Obj("a"), Obj("aa")))

        val actual = BeanUtil.createMapAndCopy(src)
        assertThat(actual["name"] as String, `is`("なまえ"))
        assertThat(actual["strs"] as Array<*>, arrayContaining<Any>("a", "1", "aaa"))
        assertThat(actual["nums"] as Array<*>, arrayContaining<Any>(1, 10, 100))
        assertThat(actual["objects"] as Array<*>, arrayContaining<Any>(Obj("a"), Obj("aa")))
    }

    @Test
    fun `Map→オブジェクト_List→Mapで元のMapが復元されること`() {
        val src = mapOf(
            "name" to "なまえ",
            "strs" to listOf("a", "1", "aaa"),
            "nums" to arrayListOf(1, 10, 100),
            "objects" to LinkedList(listOf(Obj("a"), Obj("aa"))))

        val obj = BeanUtil.createAndCopy(WithList::class.java, src)
        val actual = BeanUtil.createMapAndCopy(obj)

        assertThat(actual, `is`(src))
    }

    @Test
    fun `Map→オブジェクト_Array→Mapで元のMapが復元されること`() {
        val src = mapOf<String, Any>(
            "name" to "なまえ",
            "strs" to arrayOf("a", "1", "aaa"),
            "nums" to arrayOf(1, 10, 100),
            "objects" to arrayOf(Obj("a"), Obj("aa")))

        val obj = BeanUtil.createAndCopy(WithArray::class.java, src)
        val actual = BeanUtil.createMapAndCopy(obj)

        assertThat(actual["name"] as String, `is`("なまえ"))
        assertThat(actual["strs"] as Array<*>, arrayContaining<Any>("a", "1", "aaa"))
        assertThat(actual["nums"] as Array<*>, arrayContaining<Any>(1, 10, 100))
        assertThat(actual["objects"] as Array<*>, arrayContaining<Any>(Obj("a"), Obj("aa")))
    }
}

