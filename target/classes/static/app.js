const apiBase = "http://localhost:8080"; // Cambia esto si tu backend tiene otro puerto

async function cargarLaboratorios() {
    try {
        const response = await fetch(`${apiBase}/laboratorios`);
        if (!response.ok) throw new Error("Error al obtener los laboratorios");

        const laboratorios = await response.json();

        ["laboratorioSelect", "laboratorioReserva"].forEach(id => {
            const select = document.getElementById(id);
            select.innerHTML = "<option value=''>Seleccione un laboratorio</option>";
            laboratorios.forEach(lab => {
                const option = document.createElement("option");
                option.value = lab.id;
                option.textContent = lab.nombre;
                select.appendChild(option);
            });
        });
    } catch (error) {
        console.error("Error al cargar los laboratorios:", error);
        alert("No se pudieron cargar los laboratorios.");
    }
}

async function consultarDisponibilidad() {
    const idLab = document.getElementById("laboratorioSelect").value;
    const fecha = document.getElementById("fechaConsulta").value;
    const hora = document.getElementById("horaConsulta").value;

    if (!idLab || !fecha || !hora) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    try {
        const response = await fetch(`${apiBase}/reservas`);
        if (!response.ok) throw new Error("Error al obtener reservas");

        const reservas = await response.json();

        // Convertir la fecha ingresada y la de la reserva a un formato comparable
        const fechaIngresada = new Date(fecha).toISOString().split("T")[0];

        const reservada = reservas.some(r =>
            r.idLaboratorio === idLab &&
            r.fecha.split("T")[0] === fechaIngresada &&
            r.horaInicio <= hora &&
            r.horaFin > hora
        );

        alert(reservada ? "El laboratorio está reservado." : "El laboratorio está disponible.");
    } catch (error) {
        console.error("Error al consultar disponibilidad:", error);
        alert("No se pudo verificar la disponibilidad.");
    }
}

async function reservarLaboratorio() {
    const reserva = {
        idLaboratorio: document.getElementById("laboratorioReserva").value,
        fecha: document.getElementById("fechaReserva").value,
        horaInicio: document.getElementById("horaInicioReserva").value,
        horaFin: document.getElementById("horaFinReserva").value,
        proposito: document.getElementById("propositoReserva").value,
        usuario: document.getElementById("usuarioReserva").value,
        estado: "Confirmada"
    };

    if (!reserva.idLaboratorio || !reserva.fecha || !reserva.horaInicio || !reserva.horaFin || !reserva.proposito || !reserva.usuario) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    try {
        const response = await fetch(`${apiBase}/reservas`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reserva)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Error al crear la reserva");
        }

        alert("Reserva creada con éxito!");
    } catch (error) {
        console.error("Error al reservar laboratorio:", error);
        alert("No se pudo crear la reserva.");
    }
}

async function cancelarReserva() {
    const id = document.getElementById("idReservaCancelar").value;

    if (!id) {
        alert("Por favor, ingrese el ID de la reserva.");
        return;
    }

    try {
        const response = await fetch(`${apiBase}/reservas/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) throw new Error("Error al cancelar la reserva");

        alert("Reserva cancelada!");
    } catch (error) {
        console.error("Error al cancelar reserva:", error);
        alert("No se pudo cancelar la reserva.");
    }
}

document.addEventListener("DOMContentLoaded", cargarLaboratorios);
