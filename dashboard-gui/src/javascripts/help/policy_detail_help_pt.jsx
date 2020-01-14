import React from "react";
import I18n from "i18n-js";

class PolicyDetailHelpPt extends React.Component {
  render() {
    return (
        <div className="form-element about">
          <h1>Como criar Políticas?</h1>

          <p>Uma política de acesso define quando um utilizador tem autorização para aceder a um Serviço com base nos atributos do utilizador fornecidos
          pela Instituição e por outros fornecedores de atributos.</p>

          <h2>Acesso</h2>

          <p>É recomendado escolher uma regra a Autorizar em vez de uma regra a Negar.</p>

          <p>O algoritmo utilizado para determinar se alguém
            tem o acesso autorizado é baseado na política de 'first-applicable'. Isto significa que a primeira regra
            válida é a regra que determina o resultado - Por exemplo Negar ou Permitir.
          </p>

          <p>Podem ser vistas <a target="_blank" rel="noopener noreferrer" href="http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-os-en.html#_Toc325047268">aqui</a> mais informações sobre as implicações ao Negar um acesso.
          </p>

          <h2>Instituição</h2>

          <p>A Instituição determina para que Instituição esta política se aplica. A Instituição neste contexto é o Fornecedor de Identidade que autenticou o utilizador.
            As políticas podem ser ligadas a nenhuma Instituição ou a várias</p>

          <br/>
          <p>
            Se mantiver o campo Instituição vazio (não selecionar nenhuma Instituição), poderá apenas efetuar Autorização de políticas a Fornecedores de Serviço que pertenção à sua Instituição ligados a este serviço. Se forem selecionadas 1 ou mais Instituições, a sua política de autorização poderá ser aplicada a qualquer fornecedor de Serviço ou a qualquer Instituição que esteja ligada.
          </p>

          <h2>Service</h2>

          <p>O Serviço determina para que Serviço se aplica esta política. Só podem ser ligadas políticas a um Serviço.</p>

          <h2>Regra</h2>

          <p>Escolha no caso de todos os atributos definidos nesta política correspondem aos atributos do utilizador ou que apenas uma correspondência seja suficiente para uma 'Permissão'</p>

          <h2>Atributos</h2>

          <p>Os atributos e os seus valores na verdade é que definem se é permitido ou não o acesso a um utilizador. Para mais
              informações sobre atributos aceda a <a target="_blank" rel="noopener noreferrer" href={I18n.t("policy_attributes.help_link")}>SURFnet wiki</a>.</p>

          <h2>Autorização de Nome de Grupo</h2>

          <p>Devem haver uma especial atenção quando se escolhe <em>urn:collab:group:surfteams.nl</em> como um atributo obrigatório.
            O valor deve ser o nome completo do grupo em que o utilizador é membro. Consulte o responsável técnico como recuperar o nome completo de um determinado grupo / equipa.</p>

        </div>
    );
  }
}

export default PolicyDetailHelpPt;
