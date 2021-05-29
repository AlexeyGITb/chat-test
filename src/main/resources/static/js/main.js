$(function () {
    var userName = "Юзер"

    let initChat = function () {
        loadMessages();
        loadUsers();
    }

    let authUser = function (){
        let name = prompt('Введите имя пользователя')
        userName = name;
        $.post('/api/users', {'name': name},function (response) {
            if(response.result){
                initChat()
            } else {
                alert('Ошибка!')
            }
        })
    }
    let loadUsers = function (){
        $.getJSON('/api/users',function (response) {
            let users = response
            let usersList = $('.right')
            console.log(users)
            for(let i in users){
                let userItem = $('<div class="user-item"></div>')
                userItem.text(users[i])
                usersList.append(userItem)
            }
        })
    }
    let loadMessages = function (){
        $.getJSON('/api/messages',function (response) {
            let messages = response;
            let messagesList = $('.msg-container')
            for(let i in messages){
                let messageItem = $('<div class="message"><b>' +
                    messages[i].time + "&nbsp;" + messages[i].name +
                    '</b> ' + messages[i].text + '</div>');
                messagesList.append(messageItem)
            }
        })
    }
    let checkAuthStatus = function () {
        $.get('/api/auth', function (response){
            if(response.result){
                userName = response.name;
                initChat()
            } else {
                authUser()
            }
        })
    }
    checkAuthStatus()

    $('.send-message').on('click',function () {
        let message = $('.message-text').val();
        let messagesList = $('.msg-container')
        $.post('/api/messages',{'message' : message} ,function (response){
            if(response.result){
                let messageItem = $('<div class="message"><b>' +
                    response.time + "&nbsp;" + userName +
                    '</b> ' + message + '</div>');
                $('.message-text').val('');
                messagesList.append(messageItem)
            } else {
               alert("Ошибка")
            }
        })

    })
})