function sendRestRequest (method, url, callback, acceptType = null, payload = null, payloadType = null, token = null, async = true) {
    let xhr = new XMLHttpRequest();
    xhr.open(method, url, async);
    if (token !== null)
        xhr.setRequestHeader("Authorization", "Bearer " + token);
    if (payloadType !== null)
        xhr.setRequestHeader("Content-Type", payloadType);
    if (acceptType !== null)
        xhr.setRequestHeader("Accept", acceptType);
    if (async) {
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                callback(xhr.responseText, xhr.status, xhr.getResponseHeader("Authorization"));
            }
        };
    }
    xhr.send(payload);
    if (!async) {
        callback(xhr.responseText, xhr.status, xhr.getResponseHeader("Authorization"));
    }
};

function handleLogin () {
    let u = document.getElementById("username").value;
    let p = document.getElementById("password").value;

    sendRestRequest(
        "post", "rest/auth/login",
        function (callResponse, callStatus, callAuthHeader) {
            if (callStatus === 200) {
                let token = callResponse;

                localStorage.setItem("token", token);
                localStorage.setItem("username", u);
                
                window.location.href = "/WMServices_GS/richieste.html";
            } else {
                alert("Login fallito: " + callStatus);
                setToken(null);
            }
        },
        null,
        "username=" + encodeURIComponent(u) + "&password=" + encodeURIComponent(p), 
        "application/x-www-form-urlencoded",
        null
    );
};

document.getElementById("accediButton").addEventListener("click", handleLogin);