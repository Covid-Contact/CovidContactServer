package cat.covidcontact.server.controllers.user

sealed class UserException : Exception() {
    class UserExisting : UserException()
    class UserNotExisting : UserException()
    class InvalidId : UserException()
}
