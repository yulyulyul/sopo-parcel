package team.sopo.parcel.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.common.exception.SopoException
import team.sopo.parcel.domain.register.RegisterProcessor
import team.sopo.parcel.domain.search.SearchProcessor
import team.sopo.parcel.domain.update.UpdateProcessor
import team.sopo.parcel.domain.update.UpdateStatus

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
        val parcel = parcelReader.getParcel(getCommand.parcelId, getCommand.userId)
        return parcelInfoMapper.of(parcel)
    }

    @Transactional(readOnly = true)
    override fun getParcels(getCommand: ParcelCommand.GetParcels): List<ParcelInfo.Main> {
        val parcels = parcelReader.getParcels(getCommand.parcelIds, getCommand.userId)
        return parcelInfoMapper.of(parcels)
    }

    @Transactional(readOnly = true)
    override fun getOngoingParcels(getCommand: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(getCommand.userId)
        return parcelInfoMapper.of(ongoingParcels)
    }

    @Transactional(readOnly = true)
    override fun getCompleteParcels(getCommand: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main> {
        val completeParcels =
            parcelReader.getCompleteParcels(getCommand.userId, getCommand.inquiryDate, getCommand.pageable)
        return parcelInfoMapper.of(completeParcels)
    }

    @Transactional(readOnly = true)
    override fun getMonthlyParcelCntList(getCommand: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelReader.getMonthlyParcelCntList(getCommand.userId)
    }

    @Transactional(readOnly = true)
    override fun getUsageInfo(getCommand: ParcelCommand.GetUsageInfo): ParcelInfo.ParcelUsage {
        val countIn2Week = parcelReader.getRegisteredCountIn2Week(getCommand.userId)
        val totalCount = parcelReader.getRegisteredParcelCount(getCommand.userId)

        return ParcelInfo.ParcelUsage(countIn2Week, totalCount)
    }

    @Transactional
    override fun changeParcelAlias(changeCommand: ParcelCommand.ChangeParcelAlias) {
        val parcel = parcelReader.getParcel(changeCommand.parcelId, changeCommand.userId)
        parcel.changeParcelAlias(changeCommand.alias)
    }

    @Transactional
    override fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel) {
        parcelReader.getParcels(deleteCommand.parcelIds, deleteCommand.userId).parallelStream()
            .forEach{ parcel ->
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
            parcelReader.getParcel(refreshCommand.parcelId, refreshCommand.userId).also { it.verifyRefreshable() }
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
        val ongoingParcels = parcelReader.getOngoingParcels(refreshCommand.userId)
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
}