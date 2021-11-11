package team.sopo.parcel.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.dto.UsageInfoDTO

@Service
class GetUsageInfoService (private val parcelRepository: ParcelRepository){
    @Transactional(readOnly = true)
    fun getUsageInfo(userId: String): UsageInfoDTO{
        val countIn2Week = parcelRepository.getRegisterParcelCountIn2Week(userId)
        val totalCount = parcelRepository.getRegisterParcelCount(userId)

        return UsageInfoDTO(countIn2Week,totalCount)
    }
}