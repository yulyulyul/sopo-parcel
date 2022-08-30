package team.sopo.domain.parcel

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.common.exception.SopoException
import team.sopo.domain.parcel.register.RegisterProcessor
import team.sopo.domain.parcel.search.SearchProcessor
import team.sopo.domain.parcel.update.UpdateProcessor
import team.sopo.domain.parcel.update.UpdateStatus

@Service
class ParcelServiceImpl(
    private val parcelReader: ParcelReader,
    private val searchProcessor: SearchProcessor,
    private val updateProcessor: UpdateProcessor,
    private val registerProcessor: RegisterProcessor,
    private val parcelInfoMapper: ParcelInfoMapper
) : ParcelService {

    @Transactional(readOnly = true)
    override fun getParcel(getCommand: ParcelCommand.GetParcel): ParcelInfo.Main {
        val parcel = parcelReader.getParcel(getCommand.parcelId, getCommand.userToken)
        return parcelInfoMapper.of(parcel)
    }

    @Transactional(readOnly = true)
    override fun getParcels(getCommand: ParcelCommand.GetParcels): List<ParcelInfo.Main> {
        val parcels = parcelReader.getParcels(getCommand.parcelIds, getCommand.userToken)
        return parcelInfoMapper.of(parcels)
    }

    @Transactional
    override fun reporting(command: ParcelCommand.Reporting) {
        val parcels = parcelReader.getParcels(command.parcelIds, command.userToken)
        parcels.forEach { it.reporting() }
    }

    @Transactional(readOnly = true)
    override fun getOngoingParcels(getCommand: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(getCommand.userToken)
        return parcelInfoMapper.of(ongoingParcels)
    }

    @Transactional(readOnly = true)
    override fun getCompleteParcels(getCommand: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main> {
        val completeParcels =
            parcelReader.getCompleteParcels(
                getCommand.userToken,
                getCommand.inquiryDate,
                getCommand.pageable,
                getCommand.itemCnt
            )
        return parcelInfoMapper.of(completeParcels)
    }

    @Transactional(readOnly = true)
    override fun getMonthlyParcelCntList(getCommand: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelReader.getMonthlyParcelCntList(getCommand.userToken)
    }

    @Transactional(readOnly = true)
    override fun getMonthlyPageInfo(getCommand: ParcelCommand.GetMonthlyPageInfo): ParcelInfo.MonthlyPageInfo {
        val monthlyHistory = parcelReader.getMonthlyParcelCntList(getCommand.userToken)

        return if (getCommand.isFirstPage()) {
            monthlyHistory.getFirstPageDate()
        } else {
            monthlyHistory.getCursorDateInfo(getCommand.cursorDate)
        }
    }

    @Transactional(readOnly = true)
    override fun getUsageInfo(getCommand: ParcelCommand.GetUsageInfo): ParcelInfo.ParcelUsage {
        val countIn2Week = parcelReader.getRegisteredCountIn2Week(getCommand.userToken)
        val totalCount = parcelReader.getRegisteredParcelCount(getCommand.userToken)

        return ParcelInfo.ParcelUsage(countIn2Week, totalCount)
    }

    @Transactional
    override fun changeParcelAlias(changeCommand: ParcelCommand.ChangeParcelAlias) {
        val parcel = parcelReader.getParcel(changeCommand.parcelId, changeCommand.userToken)
        parcel.changeParcelAlias(changeCommand.alias)
    }

    @Transactional
    override fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel) {
        parcelReader.getParcels(deleteCommand.parcelIds, deleteCommand.userToken).parallelStream()
            .forEach { parcel ->
                parcel.verifyDeletable(deleteCommand)
                parcel.inactivate()
            }
    }

    @Transactional
    override fun registerParcel(registerCommand: ParcelCommand.RegisterParcel): ParcelInfo.Main {
        val searchResult = searchProcessor.search(registerCommand.toSearchRequest())
        val initParcel = registerCommand.toEntity(searchResult)
        val parcel = registerProcessor.register(registerCommand.toRegisterRequest(parcel = initParcel))

        return parcelInfoMapper.of(parcel)
    }

    @Transactional
    override fun singleRefresh(refreshCommand: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel {
        val originalParcel =
            parcelReader.getParcel(refreshCommand.parcelId, refreshCommand.userToken).also { it.verifyRefreshable() }
        val searchResult = searchProcessor.search(refreshCommand.toSearchRequest(originalParcel))
        val refreshedParcel = refreshCommand.toEntity(searchResult, originalParcel)

        val processResult = updateProcessor.update(refreshCommand.toUpdateRequest(originalParcel, refreshedParcel))

        return ParcelInfo.RefreshedParcel(
            parcel = parcelInfoMapper.of(processResult.updatedParcel),
            isUpdated = processResult.updateStatus == UpdateStatus.SUCCESS_TO_UPDATE
        )
    }

    @Transactional
    override fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<Long> {
        val ongoingParcels = parcelReader.getOngoingParcels(refreshCommand.userToken)
        return ongoingParcels
            .filter { parcel -> parcel.isEntireRefreshable() }
            .filter { parcel ->
                try {
                    singleRefresh(refreshCommand.toRefreshRequest(parcel.id)).isUpdated
                } catch (e: SopoException) {
                    false
                }
            }
            .map(Parcel::id)
            .toList()
    }

    private fun List<ParcelInfo.MonthlyParcelCnt>.getCursorDateInfo(cursorDate: String?): ParcelInfo.MonthlyPageInfo {
        forEachIndexed { index, monthlyParcelCnt ->
            val hasPrevious = index != 0
            val hasNext = index != (this.size - 1)

            if (monthlyParcelCnt.time == cursorDate) {
                return ParcelInfo.MonthlyPageInfo(
                    hasPrevious = hasPrevious,
                    previousDate = if (hasPrevious) this[index - 1].time else null,
                    hasNext = hasNext,
                    nextDate = if (hasNext) this[index + 1].time else null,
                    cursorDate = monthlyParcelCnt.time
                )
            }
        }
        return ParcelInfo.MonthlyPageInfo.getEmptyData()
    }
    private fun List<ParcelInfo.MonthlyParcelCnt>.getFirstPageDate(): ParcelInfo.MonthlyPageInfo {
        val hasNext = this.size > 1
        return if (this.isNotEmpty()) {
            ParcelInfo.MonthlyPageInfo(
                hasPrevious = false,
                previousDate = null,
                hasNext = hasNext,
                nextDate = if (hasNext) this[1].time else null,
                cursorDate = this[0].time
            )
        } else {
            ParcelInfo.MonthlyPageInfo.getEmptyData()
        }
    }
}