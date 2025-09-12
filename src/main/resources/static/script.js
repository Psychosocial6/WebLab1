document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("button");
    const text_field = document.getElementById("Ytext");
    const server_port = 25777;

    text_field.addEventListener("input", () => {
       validate(text_field.value);
    });

    function validate(value) {
        const val = value.trim();

        if (val === "") {
            button.disabled = true;
            text_field.setCustomValidity("Число не введено");
            text_field.reportValidity();
            return;
        }

        const re = /^-?(\d+([.,]\d*)?|[.,]\d+)$/;
        if (!re.test(val)) {
            text_field.setCustomValidity("Введено не число");
            button.disabled = true;
            text_field.reportValidity();
            return;
        }

        const num = parseFloat(val.replace(',', '.'));

        if (num <= 3 && num >= -3) {
            text_field.setCustomValidity("");
            button.disabled = false;
            text_field.reportValidity();
        }

        else {
            text_field.setCustomValidity("Число не входит в диапазон");
            button.disabled = true;
            text_field.reportValidity();
        }
    }

    function sendRequest(url, data) {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Ошибка запроса: ${response.status}`);
                }
                return response.json();
            });
    }

    function button_click() {
        const x_radio = document.querySelector('input[name="X"]:checked');
        let x_value = x_radio.value;
        let y_value = text_field.value;
        const r_radio = document.querySelector('input[name="R"]:checked');
        let r_value = r_radio.value;

        const data = { x: x_value, y: y_value, r: r_value };

        sendRequest('/api/', data)
            .then(result => {
                console.log("Ответ от сервера:", result);
            })
            .catch(error => {
                console.error("Произошла ошибка:", error);
            });
    }
    button.addEventListener("click", button_click);
});
