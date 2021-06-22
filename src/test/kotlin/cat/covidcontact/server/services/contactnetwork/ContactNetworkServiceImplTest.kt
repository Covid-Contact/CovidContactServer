package cat.covidcontact.server.services.contactnetwork

import cat.covidcontact.server.controllers.CovidContactException
import cat.covidcontact.server.isEqualTo
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetwork
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkRepository
import cat.covidcontact.server.model.nodes.contactnetwork.ContactNetworkState
import cat.covidcontact.server.model.nodes.member.Member
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostContactNetwork
import cat.covidcontact.server.services.numbercalculator.NumberCalculatorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContactNetworkServiceImplTest {
    private lateinit var contactNetworkServiceImpl: ContactNetworkServiceImpl

    @MockK
    private lateinit var contactNetworkRepository: ContactNetworkRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var numberCalculatorService: NumberCalculatorService

    private val contactNetworkPostName = "ContactNetwork"
    private val randomNumber = 1234
    private val contactNetworkName = "$contactNetworkPostName#$randomNumber"
    private val email = "albert@gmail.com"
    private val username = "Albert#1234"
    private val accessCode = "1234"
    private val newPassword = "5678"
    private val otherUserEmail = "other@gmail.com"

    private lateinit var postContactNetwork: PostContactNetwork
    private lateinit var user: User
    private lateinit var contactNetwork: ContactNetwork
    private lateinit var otherContactNetwork: ContactNetwork

    @BeforeEach
    fun setUp() {
        contactNetworkRepository = mockk()
        userRepository = mockk()
        numberCalculatorService = mockk()

        contactNetworkServiceImpl = ContactNetworkServiceImpl(
            contactNetworkRepository,
            userRepository,
            numberCalculatorService
        )

        setUpDefaultData()
    }

    @Test
    fun `when creating network owner is not available then exception is thrown`() {
        postContactNetwork.ownerEmail = null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.createContactNetwork(postContactNetwork)
        }
    }

    @Test
    fun `when creating network owner does not exist then exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.createContactNetwork(postContactNetwork)
        }
    }

    @Test
    fun `when creating network if owner has a similar one then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork))
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.createContactNetwork(postContactNetwork)
        }
    }

    @Test
    fun `when creating network there is not any error then it is created`() {
        every { userRepository.findByEmail(any()) } returns user
        every { numberCalculatorService.generateRandomNumber() } returns randomNumber
        every { userRepository.save(any()) } returns user

        val result = contactNetworkServiceImpl.createContactNetwork(postContactNetwork)
        assertThat(result.name, isEqualTo(contactNetworkName))
        assertThat(
            user.contactNetworks.map { member -> member.contactNetwork.name }
                .contains(contactNetworkName),
            isEqualTo(true)
        )

        verify {
            userRepository.findByEmail(any())
            numberCalculatorService.generateRandomNumber()
            userRepository.save(any())
        }
    }

    @Test
    fun `when getting networks from a user that does not exist then exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.getContactNetworksFromUser(email)
        }
    }

    @Test
    fun `when getting networks from a user then the deleted ones are not returned`() {
        contactNetwork.state = ContactNetworkState.Deleted
        user.contactNetworks.add(Member(contactNetwork = contactNetwork))
        every { userRepository.findByEmail(any()) } returns user

        val result = contactNetworkServiceImpl.getContactNetworksFromUser(email)
        assertThat(result.size, isEqualTo(0))

        verify {
            userRepository.findByEmail(any())
        }
    }

    @Test
    fun `when getting networks from a user then they are returned`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork))
        every { userRepository.findByEmail(any()) } returns user

        val result = contactNetworkServiceImpl.getContactNetworksFromUser(email)
        assertThat(result.size, isEqualTo(1))

        verify {
            userRepository.findByEmail(any())
        }
    }

    @Test
    fun `when enabling user addition network does not exist then an exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.enableUserAddition(contactNetworkName, false)
        }
    }

    @Test
    fun `when enabling user addition network then its visibility is updated`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        contactNetworkServiceImpl.enableUserAddition(contactNetworkName, false)

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when generating access code network does not exist then an exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.generateAccessCode(contactNetworkName)
        }
    }

    @Test
    fun `when generating access code network then it is generated`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { numberCalculatorService.generateAccessCode() } returns accessCode
        every { contactNetworkRepository.existsContactNetworkByAccessCode(any()) } returns false
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        val code = contactNetworkServiceImpl.generateAccessCode(contactNetworkName)
        assertThat(code, isEqualTo(accessCode))

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            numberCalculatorService.generateAccessCode()
            contactNetworkRepository.existsContactNetworkByAccessCode(any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when getting network by access code it does not exist then an exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByAccessCode(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.getContactNetworkByAccessCode(accessCode)
        }
    }

    @Test
    fun `when getting network by access code then it is returned`() {
        every {
            contactNetworkRepository.findContactNetworkByAccessCode(any())
        } returns contactNetwork

        val result = contactNetworkServiceImpl.getContactNetworkByAccessCode(accessCode)
        assertThat(result.name, isEqualTo(contactNetwork.name))

        verify {
            contactNetworkRepository.findContactNetworkByAccessCode(any())
        }
    }

    @Test
    fun `when joining network user does not exist then an exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.joinContactNetwork(otherContactNetwork.name, email)
        }
    }

    @Test
    fun `when joining network user is already a member then an exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = otherContactNetwork))
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.joinContactNetwork(otherContactNetwork.name, email)
        }
    }

    @Test
    fun `when joining network it does not exist then an exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.joinContactNetwork(otherContactNetwork.name, email)
        }
    }

    @Test
    fun `when joining network it is added as a member`() {
        every { userRepository.findByEmail(any()) } returns user
        every {
            contactNetworkRepository.findContactNetworkByName(any())
        } returns otherContactNetwork
        every { userRepository.save(any()) } returns user

        contactNetworkServiceImpl.joinContactNetwork(otherContactNetwork.name, email)
        assertThat(
            user.contactNetworks.map { member -> member.contactNetwork.name }
                .contains(otherContactNetwork.name),
            isEqualTo(true)
        )

        verify {
            userRepository.findByEmail(any())
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.save(any())
        }
    }

    @Test
    fun `when exiting network user does not exist then an exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.exitContactNetwork(contactNetworkName, email)
        }
    }

    @Test
    fun `when exiting network it does not exist then an exception is thrown`() {
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.exitContactNetwork(contactNetworkName, email)
        }
    }

    @Test
    fun `when exiting network if user is owner then it is not removed`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork

        contactNetworkServiceImpl.exitContactNetwork(contactNetworkName, email)
        assertThat(
            user.contactNetworks.map { member -> member.contactNetwork.name }
                .contains(contactNetwork.name),
            isEqualTo(true)
        )
    }

    @Test
    fun `when exiting network if user is not owner then it is removed`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.removeMember(any(), any()) } returns Unit
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        contactNetworkServiceImpl.exitContactNetwork(contactNetworkName, email)

        assertThat(
            user.contactNetworks.map { member -> member.contactNetwork.name }
                .contains(contactNetwork.name),
            isEqualTo(false)
        )

        verify {
            userRepository.findByEmail(any())
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.removeMember(any(), any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when deleting network if it is does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteContactNetwork(contactNetworkName, email)
        }
    }

    @Test
    fun `when deleting network user does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteContactNetwork(contactNetworkName, email)
        }
    }

    @Test
    fun `when deleting network user is not owner then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteContactNetwork(contactNetworkName, email)
        }
    }

    @Test
    fun `when deleting network then state is updated`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.setStateToDeleted(any()) } returns Unit

        contactNetworkServiceImpl.deleteContactNetwork(contactNetworkName, email)
        assertThat(contactNetwork.state, isEqualTo(ContactNetworkState.Deleted))

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.findByEmail(any())
            contactNetworkRepository.setStateToDeleted(any())
        }
    }

    @Test
    fun `when updating network visibility if it is does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateVisibility(contactNetworkName, email, false)
        }
    }

    @Test
    fun `when updating network visibility user does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateVisibility(contactNetworkName, email, false)
        }
    }

    @Test
    fun `when updating network visibility user is not owner then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateVisibility(contactNetworkName, email, false)
        }
    }

    @Test
    fun `when updating network visibility then it is updated`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        contactNetworkServiceImpl.updateVisibility(contactNetworkName, email, false)
        assertThat(contactNetwork.isVisible, isEqualTo(false))

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.findByEmail(any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when updating network password if it is does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updatePassword(contactNetworkName, newPassword, email)
        }
    }

    @Test
    fun `when updating network password user does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updatePassword(contactNetworkName, newPassword, email)
        }
    }

    @Test
    fun `when updating network password user is not owner then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updatePassword(contactNetworkName, newPassword, email)
        }
    }

    @Test
    fun `when updating network password then it is updated`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        contactNetworkServiceImpl.updatePassword(contactNetworkName, newPassword, email)
        assertThat(contactNetwork.password, isEqualTo(newPassword))

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.findByEmail(any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when updating password protection if it is does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateIsPasswordProtected(contactNetworkName, false, email)
        }
    }

    @Test
    fun `when updating password protection user does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateIsPasswordProtected(contactNetworkName, false, email)
        }
    }

    @Test
    fun `when updating password protection user is not owner then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.updateIsPasswordProtected(contactNetworkName, false, email)
        }
    }

    @Test
    fun `when updating password protection then it is updated`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user
        every { contactNetworkRepository.save(any()) } returns contactNetwork

        contactNetworkServiceImpl.updateIsPasswordProtected(contactNetworkName, false, email)
        assertThat(contactNetwork.isPasswordProtected, isEqualTo(false))

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.findByEmail(any())
            contactNetworkRepository.save(any())
        }
    }

    @Test
    fun `when getting contact network if not member and user is member then list is empty`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { userRepository.getAllMembersFromContactNetwork(any()) } returns listOf(user)

        val contactNetworks = contactNetworkServiceImpl.getContactNetworkIfNotMember(
            contactNetworkName,
            email
        )

        assertThat(contactNetworks.size, isEqualTo(0))
    }

    @Test
    fun `when getting contact network if not member and it does not exist then list is empty`() {
        every { userRepository.getAllMembersFromContactNetwork(any()) } returns emptyList()
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        val contactNetworks = contactNetworkServiceImpl.getContactNetworkIfNotMember(
            contactNetworkName,
            email
        )

        assertThat(contactNetworks.size, isEqualTo(0))
    }

    @Test
    fun `when getting contact network if not member then list contains it`() {
        every { userRepository.getAllMembersFromContactNetwork(any()) } returns emptyList()
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork

        val contactNetworks = contactNetworkServiceImpl.getContactNetworkIfNotMember(
            contactNetworkName,
            email
        )

        assertThat(contactNetworks.size, isEqualTo(1))
    }

    @Test
    fun `when deleting member if it is does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteMember(contactNetworkName, otherUserEmail, email)
        }
    }

    @Test
    fun `when deleting member user does not exist then exception is thrown`() {
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns null

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteMember(contactNetworkName, otherUserEmail, email)
        }
    }

    @Test
    fun `when deleting member user is not owner then exception is thrown`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = false))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user

        assertThrows<CovidContactException> {
            contactNetworkServiceImpl.deleteMember(contactNetworkName, otherUserEmail, email)
        }
    }

    @Test
    fun `when deleting member then member is deleted`() {
        user.contactNetworks.add(Member(contactNetwork = contactNetwork, isOwner = true))
        every { contactNetworkRepository.findContactNetworkByName(any()) } returns contactNetwork
        every { userRepository.findByEmail(any()) } returns user
        every { userRepository.removeMember(any(), any()) } returns Unit

        contactNetworkServiceImpl.deleteMember(contactNetworkName, otherUserEmail, email)

        verify {
            contactNetworkRepository.findContactNetworkByName(any())
            userRepository.findByEmail(any())
            userRepository.removeMember(any(), any())
        }
    }

    private fun setUpDefaultData() {
        postContactNetwork = PostContactNetwork(
            name = contactNetworkPostName,
            password = "1234",
            ownerEmail = email,
            ownerUsername = username,
            isVisible = true,
            isPasswordProtected = true,
            accessCode = "123456",
            state = ContactNetworkState.Normal.toString()
        )

        user = User(
            email = email,
            username = username
        )

        contactNetwork = ContactNetwork(
            name = contactNetworkName
        )

        otherContactNetwork = ContactNetwork(
            name = "${contactNetworkPostName}2#1234"
        )
    }
}
