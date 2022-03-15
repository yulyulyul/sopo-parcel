package team.sopo.infrastructure.parcel.tracker.sweet

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "sweet-tracker",
    url = "https://info.sweettracker.co.kr/"
)
interface SweetTrackerClient {
    @GetMapping("api/v1/trackingInfo")
    fun getTrackingInfo(
        @RequestParam("t_key") t_key: String,
        @RequestParam("t_code") t_code: String,
        @RequestParam("t_invoice") t_invoice: String
    ): SweetTrackingInfo
}