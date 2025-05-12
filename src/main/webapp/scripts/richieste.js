$(document).ready(function () {
    var token = localStorage.getItem("token"); 
    var username = localStorage.getItem("username"); 

    if (!token) {
        alert("Per favore, effettua nuovamente il login.");
        window.location.href = "index.html";
        return;
    }

    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('it-IT', options);
    }

    $.ajax({
        url: "/WMServices_GS/rest/richieste/in_corso?ID_Ordinante="+username,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function (data) {
            const container = $("#tabellaRichieste tbody");
            container.empty();

            data.forEach(function (richiesta) {
                const richiestaHTML = `
                <tr onclick="location.href='detail_richiesta.html?richiesta=${richiesta.ID}'">
                    <td>${richiesta.ID}</td>
                    <td>${richiesta.categoria}</td>
                    <td>${formatDate(richiesta.data)}</td>
                    <td>${richiesta.stato}</td>
                </tr>
                `;
                container.append(richiestaHTML);
            });
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Si prega di effettuare l'accesso.");
                window.location.href = "index.html";
                return;
            }
            else if (xhr.status === 404) {
                alert("Non ci sono richieste in corso per questo utente!");
            }
            else {  
                alert("Errore durante il caricamento delle richieste. Riprova più tardi.");
            }                
        }
    });
});
