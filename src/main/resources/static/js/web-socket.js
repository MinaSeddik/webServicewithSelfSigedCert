var stompClient = null;

function connect() {

    console.log("Connecting to http://localhost:8080/mywebsockets");
    var socket = new SockJS('http://localhost:8080/mywebsockets');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/news', function(messageOutput) {

            console.log('Receiving : [/topic/news]' + messageOutput);
//            showMessageOutput(JSON.parse(messageOutput.body));
            showMessageOutput(messageOutput.body);
        });

    });

}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function showMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
//    p.appendChild(document.createTextNode(messageOutput.from + ": " + messageOutput.text + " (" + messageOutput.time + ")"));
    p.appendChild(document.createTextNode(messageOutput));
    response.appendChild(p);
}


function sendMessage() {
    var text = document.getElementById('text').value;
//    stompClient.send("/app/chat", {}, JSON.stringify({'from':from, 'text':text}));
    console.log('Will send ' + text);
    stompClient.send("/app/news", {}, text);
}


