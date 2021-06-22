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

package cat.covidcontact.server.model.authentication.applicationuser

import javax.persistence.*

@Entity
@Table(name = "users")
@Suppress("JpaDataSourceORMInspection")
data class ApplicationUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long,
    var email: String,
    var password: String,

    @Column(name = "is_verified")
    var isVerified: Boolean = false
)
