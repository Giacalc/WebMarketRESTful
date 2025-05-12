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
        url: "/WMServices_GS/rest/proposte?ID_Ordinante="+username,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function (data) {
            const container = $("#tabellaProposte tbody");
            container.empty();

            data.forEach(function (proposta) {
                const propostaHTML = `
                <tr onclick="location.href='detail_proposta.html?proposta=${proposta.ID}'" >
                    <td>${proposta.ID}</td>
                    <td>${proposta.richiesta.ID}</td>
                    <td>${formatDate(proposta.data)}</td>
                    <td>${proposta.stato}</td>
                </tr>
                `;
                container.append(propostaHTML);
            });
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Si prega di effettuare l'accesso.");
                window.location.href = "index.html";
                return;
            }
            else if (xhr.status === 404) {
                alert("Non ci sono proposte in corso per questo utente!");
            }
            else {  
                alert("Errore durante il caricamento delle proposte. Riprova più tardi.");
            }                        
        }
    });
});
