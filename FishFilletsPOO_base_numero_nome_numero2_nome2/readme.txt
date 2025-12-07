===========================================================
PROJETO DE PROGRAMAÇÃO ORIENTADA A OBJETOS (Ano Letivo 2025/2026)
JOGO: FISH FILLETS NG - VERSÃO JAVA
===========================================================

GRUPO: JG2

Daniel de Matos Masqueiro Nº129853
Duarte Franco Baptista de Sousa de Oliveira Nº129851

OPÇÕES DE DESIGN E IMPLEMENTAÇÃO:
-----------------------------------------------------------

1. LÓGICA DE INTERAÇÃO COM "HOLEDWALL" (PAREDE COM BURACO)
   Implementámos regras específicas de física para quando o Peixe Pequeno 
   se encontra "dentro" da Parede com Buraco:
   
   - Esmagamento: O facto de o peixe estar abrigado na parede não o torna 
     imune à gravidade externa. Se um objeto pesado (como uma Pedra) cair 
     sobre a Parede com Buraco onde o peixe está, o peixe é esmagado e o 
     jogo termina.
     
   - Suporte de Bombas: Existe uma exceção lógica para as bombas. Se uma 
     Bomba cair sobre uma Parede com Buraco ocupada pelo Peixe Pequeno, 
     consideramos que o peixe está ativamente a "suportar" a bomba. 
     Consequentemente, a bomba não explode, permitindo estratégias onde o 
     peixe segura o explosivo.

2. FÍSICA DA ARMADILHA (TRAP)
   A Armadilha possui um comportamento dual dependendo da origem do movimento:
   
   - Transponível: O Peixe Pequeno consegue atravessar a armadilha livremente 
     sem sofrer danos, passando por "dentro" da sua estrutura.
     
   - Letal por Gravidade: Se a armadilha cair (ação da gravidade) sobre o 
     Peixe Pequeno, a estrutura pesada da armadilha esmaga-o, resultando 
     na morte do peixe.

3. SISTEMA DE PONTUAÇÃO (HIGHSCORE)
   O critério de classificação no quadro de melhores pontuações foi desenhado 
   para valorizar a rapidez de raciocínio:
   - Critério Principal: Tempo de jogo (menor tempo é melhor).
   - Desempate: Em caso de igualdade no tempo, vence quem tiver realizado 
     o menor número total de movimentos.

4. FEEDBACK VISUAL
   Adicionámos um sprite específico ("deadfish.png") que é renderizado 
   imediatamente quando um dos peixes morre, fornecendo feedback visual 
   claro ao jogador sobre a causa do fim do jogo antes do reinício do nível.



HIERARQUIA DE CLASSES E ABSTRAÇÃO:
-----------------------------------------------------------
   Para garantir a máxima reutilização de código e evitar duplicação de lógica 
   (especialmente na movimentação e gravidade), estruturámos o projeto com 
   base numa hierarquia de classes abstratas robusta:

   - GameObject (Abstrata):
     Classe base de todas as entidades. Centraliza a gestão de estado comum, 
     como a posição (Point2D), a referência à sala (Room) e as propriedades 
     booleanas base (isSolid, isSupport).

   - MovableElement (Abstrata):
     Estende GameObject. Isola a lógica de "capacidade de movimento". 
     É aqui que definimos o método `move()` e a lógica base da `processGravity()`, 
     permitindo que tanto personagens como objetos caiam sem precisarmos de 
     reescrever código para cada um.

   - GameCharacter (Abstrata):
     Estende MovableElement. Focada nos agentes vivos (Peixes e Caranguejo).
     Adiciona propriedades como a direção do olhar (`facingDirection`) e estado 
     de vida (`isDead`), além de implementar a interface `PushAgent` para 
     gerir quem empurra o quê.

   - MovableObject (Abstrata):
     Estende MovableElement. Focada nos itens inanimados (Pedras, Bombas, 
     Boias). Implementa a interface `Heavy` e define a lógica de "cadeia de 
     empurrão" (`canPushChain`), permitindo verificar se um objeto pode ser 
     movido por um personagem dependendo do que está atrás dele.


ARQUITETURA E INTERFACES:
-----------------------------------------------------------
Para evitar agrupamento excessivo e promover o polimorfismo, a estrutura 
do jogo baseia-se num conjunto de interfaces que definem capacidades:

1. Interfaces de Propriedades Físicas (Solid, Support, Heavy)
   - Utilizadas para definir as características fundamentais dos objetos 
     no GameEngine. Em vez de verificar tipos concretos (ex: "instanceof Stone"), 
     o motor verifica se o objeto "é Pesado" ou "é Sólido". Isto permite 
     criar novos objetos no futuro sem alterar a lógica de física.

2. Interface Transpassable
   - Motivo: Necessária para objetos que são sólidos para a maioria das 
     entidades, mas permeáveis para outras específicas.
   - Aplicação: A 'HoledWall' e a 'Trap' implementam esta interface. 
     O método 'isPassableFor(GameObject obj)' permite definir logicamente 
     quem pode atravessar o quê (ex: Peixe Pequeno atravessa a Parede com Buraco).

3. Interface Dangerous
   - Motivo: Separar a lógica de "matar" da classe do objeto.
   - Aplicação: Tanto a 'Trap' como o 'Crab' (Caranguejo) implementam esta 
     interface. Permite ao motor de jogo verificar genericamente se um objeto 
     é letal para o personagem atual através do método 'isLethalTo()'.

4. Interface PushAgent
   - Motivo: Abstrair a capacidade de empurrar objetos.
   - Aplicação: Implementada pelos personagens. Define a força do agente 
     e se este tem capacidade para empurrar objetos pesados, facilitando 
     a distinção entre o Peixe Grande (forte) e o Pequeno (fraco).


INSTRUÇÕES DE JOGO:
-----------------------------------------------------------
[Setas]: Mover o peixe ativo.
[Espaço]: Alternar entre o SmallFish e o BigFish.
[R]: Reiniciar o nível atual.