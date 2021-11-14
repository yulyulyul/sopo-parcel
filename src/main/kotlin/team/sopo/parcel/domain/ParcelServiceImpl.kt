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
    private val parcelInfoMapper: ParcelInfoMapper): ParcelService {

    @Transactional(readOnly = true)
    override fun retrieveParcel(command: ParcelCommand.GetParcel): ParcelInfo.Main {
        val parcel = parcelReader.getParcel(command.parcelId, command.userId)
        return parcelInfoMapper.of(parcel)
    }

    @Transactional(readOnly = true)
    override fun retrieveOngoingParcels(command: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(command.userId)
        return parcelInfoMapper.of(ongoingParcels)
    }

    @Transactional(readOnly = true)
    override fun retrieveCompleteParcels(command: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main> {
        val completeParcels = parcelReader.getCompleteParcels(command.userId, command.inquiryDate, command.pageable)
        return parcelInfoMapper.of(completeParcels)
    }

    @Transactional(readOnly = true)
    override fun retrieveMonthlyParcelCntList(command: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt> {
        return parcelReader.getMonthlyParcelCntList(command.userId)
    }

    @Transactional(readOnly = true)
    override fun retrieveUsageInfo(getUsageInfoCommand: ParcelCommand.GetUsageInfo): ParcelInfo.UsageInfo {
        val countIn2Week = parcelReader.getRegisteredCountIn2Week(getUsageInfoCommand.userId)
        val totalCount = parcelReader.getRegisteredParcelCount(getUsageInfoCommand.userId)

        return ParcelInfo.UsageInfo(countIn2Week, totalCount)
    }

    @Transactional
    override fun changeParcelAlias(changeAliasCommand: ParcelCommand.ChangeParcelAlias) {
        val parcel = parcelReader.getParcel(changeAliasCommand.parcelId, changeAliasCommand.userId)
        parcel.changeParcelAlias(changeAliasCommand.alias)
    }

    @Transactional
    override fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel) {
        deleteCommand.parcelIds.stream()
            .forEach { parcelId -> parcelReader.getParcel(parcelId, deleteCommand.userId).inactivate() }
    }

    @Transactional
    override fun registerParcel(registerCommand: ParcelCommand.RegisterParcel): ParcelInfo.Main {
        val searchResult = searchProcessor.search(ParcelCommand.SearchRequest(registerCommand.userId, registerCommand.carrier, registerCommand.waybillNum))
        val parcel = registerCommand.toEntity(searchResult)
        registerProcessor.register(ParcelCommand.RegisterRequest(registerCommand.userId, registerCommand.carrier, registerCommand.waybillNum, parcel))

        return parcelInfoMapper.of(parcel)
    }

    @Transactional
    override fun singleRefresh(refreshCommand: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel {
        val originalParcel = parcelReader.getParcel(refreshCommand.parcelId, refreshCommand.userId)
        val searchResult = searchProcessor.search(refreshCommand.toSearchRequest(originalParcel))
        val refreshedParcel = refreshCommand.toEntity(searchResult, originalParcel)
        val updateResult = updateProcessor.update(refreshCommand.toUpdateRequest(originalParcel, refreshedParcel))

        return ParcelInfo.RefreshedParcel(parcelInfoMapper.of(refreshedParcel), updateResult == UpdateResult.SUCCESS_TO_UPDATE)
    }

    @Transactional
    override fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<ParcelInfo.Main> {
        val ongoingParcels = parcelReader.getOngoingParcels(refreshCommand.userId)
        return ongoingParcels
                .filter { parcel -> parcel.deliveryStatus != Parcel.DeliveryStatus.ORPHANED }
                .filter { parcel ->
                    try{
                        singleRefresh(ParcelCommand.SingleRefresh(refreshCommand.userId, parcel.id)).isUpdated
                    }
                    catch (e: SopoException){
                        false
                    }
                }
                .map { parcel -> parcelInfoMapper.of(parcel) }
                .toList()
    }
}