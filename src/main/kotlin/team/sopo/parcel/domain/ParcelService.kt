package team.sopo.parcel.domain

interface ParcelService {
    fun getParcel(getCommand: ParcelCommand.GetParcel): ParcelInfo.Main
    fun getParcels(getCommand: ParcelCommand.GetParcels): List<ParcelInfo.Main>
    fun getOngoingParcels(getCommand: ParcelCommand.GetOngoingParcels): List<ParcelInfo.Main>
    fun getCompleteParcels(getCommand: ParcelCommand.GetCompleteParcels): List<ParcelInfo.Main>
    fun getMonthlyParcelCntList(getCommand: ParcelCommand.GetMonthlyParcelCnt): List<ParcelInfo.MonthlyParcelCnt>
    fun getUsageInfo(getCommand: ParcelCommand.GetUsageInfo): ParcelInfo.ParcelUsage
    fun changeParcelAlias(changeCommand: ParcelCommand.ChangeParcelAlias)
    fun deleteParcel(deleteCommand: ParcelCommand.DeleteParcel)
    fun registerParcel(registerCommand: ParcelCommand.RegisterParcel): ParcelInfo.Main
    fun singleRefresh(refreshCommand: ParcelCommand.SingleRefresh): ParcelInfo.RefreshedParcel
    fun entireRefresh(refreshCommand: ParcelCommand.EntireRefresh): List<Long>
}