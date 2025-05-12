// dopo le richieste asincrone
 document.addEventListener("DOMContentLoaded", () => {
    const footer = document.getElementById("footer");
    const checkFooterPosition = () => {
        const isScrollActive = document.documentElement.clientHeight < document.documentElement.scrollHeight;

        if (isScrollActive) {
          footer.classList.remove("w3-bottom"); 
        } else {
          footer.classList.add("w3-bottom");
        }
    };

    checkFooterPosition();
  
    const observer = new MutationObserver((mutations) => {
        mutations.forEach(() => {
            checkFooterPosition();
        });
    });

    const targetNode = document.body;
    const config = { childList: true, subtree: true };

    observer.observe(targetNode, config);

    window.addEventListener("resize", checkFooterPosition);
});


function logout() {
    var token = localStorage.getItem("token"); 

    if (!token) {
        alert("Si prega di effetuare l'accesso.");
        window.location.href = "index.html";
        return;
    }

    $.ajax({
        url: "/WMServices_GS/rest/auth/logout",
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function () {
            localStorage.removeItem("token");
            alert("Disconnessione effettuata correttamente.");
            window.location.href = "index.html";
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Si prega di effettuare l'accesso.");
                window.location.href = "index.html";
                return;
            }
            else {  
                alert("Errore durante il logout.");
            }             
        }
    });
}