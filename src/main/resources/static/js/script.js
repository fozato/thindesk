// Abre/fecha o menu lateral em telas pequenas
(function () {
    const botao = document.querySelector('.toggle-btn');
    const sidebar = document.getElementById('sidebar');
    if (!botao || !sidebar) {
        return;
    }

    function definir(aberto) {
        sidebar.classList.toggle('active', aberto);
        botao.setAttribute('aria-expanded', String(aberto));
        botao.setAttribute('aria-label', aberto ? 'Fechar menu' : 'Abrir menu');
        botao.querySelector('i').className = aberto ? 'bi bi-x-lg' : 'bi bi-list';
    }

    botao.addEventListener('click', () => definir(!sidebar.classList.contains('active')));

    // Fecha ao clicar fora do menu ou ao apertar Esc
    document.addEventListener('click', (e) => {
        if (sidebar.classList.contains('active') && !sidebar.contains(e.target) && !botao.contains(e.target)) {
            definir(false);
        }
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            definir(false);
        }
    });
})();
