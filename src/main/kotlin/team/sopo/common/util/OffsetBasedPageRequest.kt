package team.sopo.common.util

import org.apache.commons.lang.builder.ToStringBuilder
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable

class OffsetBasedPageRequest : Pageable, Serializable {
    private val serialVersionUID = -25822477129613575L

    private var limit = 0
    private var offset:Long = 0
    private lateinit var sort: Sort

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be {@literal null}.
     */
    constructor(offset: Int, limit: Int, sort: Sort){
        if(offset < 0){
            throw IllegalArgumentException("Offset index must not be less than zero!")
        }
        if (limit < 1){
            throw IllegalArgumentException("Limit must not be less than one!")
        }
        this.limit = limit
        this.offset = offset.toLong()
        this.sort = sort
    }

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset     zero-based offset.
     * @param limit      the size of the elements to be returned.
     * @param direction  the direction of the {@link Sort} to be specified, can be {@literal null}.
     * @param properties the properties to sort by, must not be {@literal null} or empty.
     */
    constructor(offset: Int, limit: Int, direction: Sort.Direction, properties: String): this(offset, limit, Sort.by(direction, properties))

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     */
    constructor(offset: Int, limit: Int): this(offset, limit, Sort.by(Sort.Direction.DESC, "ARRIVAL_DTE"))

    override fun getPageNumber(): Int {
        return (offset/limit).toInt()
    }

    override fun getPageSize(): Int {
        return limit
    }

    override fun getOffset(): Long {
        return offset
    }

    override fun getSort(): Sort {
        return sort
    }

    override fun next(): Pageable {
        return OffsetBasedPageRequest((getOffset() + pageSize).toInt(), pageSize, getSort())
    }

    fun previous(): OffsetBasedPageRequest {
        return if (hasPrevious()) OffsetBasedPageRequest((getOffset() - pageSize).toInt(), pageSize, getSort()) else this
    }

    override fun previousOrFirst(): Pageable {
        return if (hasPrevious()) previous() else first()
    }

    override fun first(): Pageable {
        return OffsetBasedPageRequest(0, pageSize, getSort())
    }

    override fun withPage(pageNumber: Int): Pageable {
        return this
    }

    override fun hasPrevious(): Boolean {
        return offset > limit
    }

    override fun toString(): String {
        return ToStringBuilder(this)
                .append("limit", limit)
                .append("offset", offset)
                .append("sort", sort)
                .toString()
    }
}