package team.sopo.common.infra

import com.nimbusds.jwt.JWTClaimsSet

interface JwtTokenService {
    fun getClaimSet(token:  String): JWTClaimsSet
    fun getData(claimSet: JWTClaimsSet, key: String): Any
}