package team.sopo.parcel.domain

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.common.exception.SopoException
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.domain.register.RegisterProcessor
import team.sopo.parcel.domain.search.SearchProcessor
import team.sopo.parcel.domain.update.UpdateProcessor
import team.sopo.parcel.domain.update.UpdateResult

@Service
class ParcelServiceImpl(
    private val parcelReader: ParcelReader,
    private val searchProcessor: SearchProcessor,
    private val updateProcessor: UpdateProcessor,
    private val registerProcessor: RegisterProcessor,
    private val parcelInfoMapper: ParcelInfoMapper
) : ParcelService {

    @Transactional(readOnly = true)
    override fun retrieveParcel(getCommand: ParcelCommand.GetParcel): ParcelInfo.Main {
        val parcel = parcelReader.getParcel(getCommand.parcelId, getCommand.userId)
        return parcelInfoMapper.of(parcel)
    }

    @Transactional(readOnly = true)
    override fun retrieveOngoingParcels(getCommand: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(getCommand.userId)
        return parcelInfoMapper.of(ongoingParcels)
    }

    @Transactional(readOnly = true)
    override fun retrieveCompleteParcels(getCommand: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main> {
        val completeParcels = parcelReader.getCompleteParcels(getCommand.userId, getCommand.inquiryDate, getCommand.pageable)
        return parcelInfoMapper.of(completeParcels)
    }

    @Transactional(readOnly = true)
    override fun retrieveMonthlyParcelCntList(getCommand: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelReader.getMonthlyParcelCntList(getCommand.userId)
    }

    @Transactional(readOnly = true)
    override fun retrieveUsageInfo(getCommand: ParcelCommand.GetUsageInfo): ParcelInfo.UsageInfo {
        val countIn2Week = parcelReader.getRegisteredCountIn2Week(getCommand.userId)
        val totalCount = parcelReader.getRegisteredParcelCount(getCommand.userId)

        return ParcelInfo.UsageInfo(countIn2Week, totalCount)
    }

    @Transactional
    override fun changeParcelAlias(changeCommand: ParcelCommand.ChangeParcelAlias) {
        val parcel = parcelReader.getParcel(changeCommand.parcelId, changeCommand.userId)
        parcel.changeParcelAlias(changeCommand.alias)
    }

    @Transactional
    override fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel) {
        parcelReader.getParcels(deleteCommand.parcelIds).parallelStream().peek(Parcel::inactivate)

        deleteCommand.parcelIds.stream()
            .forEach { parcelId -> parcelReader.getParcel(parcelId, deleteCommand.userId).inactivate() }
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
        val originalParcel = parcelReader.getParcel(refreshCommand.parcelId, refreshCommand.userId)
        val searchResult = searchProcessor.search(refreshCommand.toSearchRequest(originalParcel))
        val refreshedParcel = refreshCommand.toEntity(searchResult, originalParcel)
        val updateResult = updateProcessor.update(refreshCommand.toUpdateRequest(originalParcel, refreshedParcel))

        return ParcelInfo.RefreshedParcel(
            parcel = parcelInfoMapper.of(refreshedParcel),
            isUpdated = updateResult == UpdateResult.SUCCESS_TO_UPDATE
        )
    }

    @Transactional
    override fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(refreshCommand.userId)
        return ongoingParcels
            .filter { parcel -> parcel.deliveryStatus != Parcel.DeliveryStatus.ORPHANED }
            .filter { parcel ->
                try {
                    singleRefresh(ParcelCommand.SingleRefresh(refreshCommand.userId, parcel.id)).isUpdated
                } catch (e: SopoException) {
                    false
                }
            }
            .map { parcel -> parcelInfoMapper.of(parcel) }
            .toList()
    }
}