/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.security.jwt.components

class Payload {
    var acr: String? = null
    var act: String? = null
    var address: String? = null
    var amr: String? = null
    var at_hash: String? = null
    var at_use_nbr: String? = null
    var attest: String? = null
    var aud: String? = null
    var auth_time: String? = null
    var azp: String? = null
    var birthdate: String? = null
    var c_hash: String? = null
    var client_id: String? = null
    var cnf: String? = null
    var dest: String? = null
    var div: String? = null
    var email: String? = null
    var email_verified: String? = null
    var events: String? = null
    var exp: String? = null
    var family_name: String? = null
    var gender: String? = null
    var given_name: String? = null
    var iat: String? = null
    var iss: String? = null
    var jcard: String? = null
    var jti: String? = null
    var locale: String? = null
    var may_act: String? = null
    var middle_name: String? = null
    var mky: String? = null
    var name: String? = null
    var nbf: String? = null
    var nickname: String? = null
    var nonce: String? = null
    var opt: String? = null
    var orig: String? = null
    var origid: String? = null
    var phone_number: String? = null
    var phone_number_verified: String? = null
    var picture: String? = null
    var preferred_username: String? = null
    var profile: String? = null
    var rph: String? = null
    var scope: String? = null
    var sid: String? = null
    var sip_callid: String? = null
    var sip_cseq_num: String? = null
    var sip_date: String? = null
    var sip_from_tag: String? = null
    var sip_via_branch: String? = null
    var sub: String? = null
    var sub_jwk: String? = null
    var toe: String? = null
    var txn: String? = null
    var updated_at: String? = null
    var vc: String? = null
    var vot: String? = null
    var vp: String? = null
    var vtm: String? = null
    var website: String? = null
    var zoneinfo: String? = null
}
