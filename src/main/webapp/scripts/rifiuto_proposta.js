$(document).ready(function () {
    var token = localStorage.getItem("token"); 
    var proposta = new URLSearchParams(window.location.search).get('proposta');

    if (!token) {
        alert("Per favore, effettua nuovamente il login.");
        window.location.href = "index.html";
        return;
    }
    
    if (!proposta) {
        alert("La proposta menzionata non è stata trovata.");
        window.location.href = "proposte.html";
        return;
    }

    $("#currentpage").text("Proposta #"+proposta);

    //rifiuta proposta
    $(document).on('click', '#rifiutaButton', function() {
        if (confirm("Sei sicuro di voler rifiutare questa proposta?")) {
            $.ajax({
                url: "/WMServices_GS/rest/proposte/"+proposta+"/rifiuta",
                type: 'PATCH',
                contentType: 'application/json',
                headers: {
                    "Authorization": "Bearer " + token
                },
                data: JSON.stringify({ motivazione: motivazione }),
                success: function() {
                    alert("Proposta rifiutata con successo.");
                    window.location.href = "proposte.html";
                }, 
                error: function (xhr) {
                    if (xhr.status === 401) {
                        alert("Si prega di effettuare l'accesso.");
                        window.location.href = "index.html";
                        return;   
                    }   
                    else {  
                        alert("Errore durante il rifiuto della proposta.");
                    }
                }
            });
        }
    });
});
