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

package cat.covidcontact.server.services.location.nominatim

import com.fasterxml.jackson.annotation.JsonProperty

data class NominatimAddress(
    @JsonProperty("city")
    var city: String? = null,

    @JsonProperty("town")
    var town: String? = null,

    @JsonProperty("village")
    var village: String? = null,

    @JsonProperty("county")
    var county: String? = null,

    @JsonProperty("municipality")
    var municipality: String? = null,

    @JsonProperty("state")
    var state: String? = null,

    @JsonProperty("country")
    var country: String? = null
)
