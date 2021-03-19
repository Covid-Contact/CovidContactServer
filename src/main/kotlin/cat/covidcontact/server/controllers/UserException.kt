package cat.covidcontact.server.controllers

sealed class UserException : Exception() {
    class UserExisting : UserException()
    class InvalidId : UserException()
}
