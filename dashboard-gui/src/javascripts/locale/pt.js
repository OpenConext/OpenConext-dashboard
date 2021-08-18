// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})

import I18n from 'i18n-js'

I18n.translations.pt = {
  code: 'PT',
  name: 'Português',
  select_locale: 'Selecionar Português',

  boolean: {
    yes: 'Sim',
    no: 'Não',
  },

  browser_not_supported: {
    title: 'O browser que está a utilizar não é suportado.',
    description_html:
      'A versão do Internet Explorer que está a utilizar não é suportada. Por favor actualize o seu browser para uma versão mais recente.',
  },

  header: {
    welcome: 'Olá,',
    links: {
      logout: 'Sair',
      exit: 'Sair',
    },
    you: 'Você',
    profile: 'Perfil',
    switch_idp: 'Escolher outro IDP',
    loginRequired: 'Please login for more information',
    super_user_switch: 'Trocar Identidade',
    welcome_txt:
      "Bem-vindo ao painel SURFconext. Nesta página, você vê quais serviços estão conectados ao SURFconext: se sua instituição permitir, você poderá acessar esses serviços usando sua conta institucional. Se você fizer login neste painel (canto superior direito), mostraremos os dados personalizados para sua instituição. Mais informações sobre este painel? Clique em 'Ajuda' no canto superior direito da tela.",
  },
  confirmation_dialog: {
    title: 'Confirme por favor',
    confirm: 'Confirmar',
    cancel: 'Cancelar',
    leavePage: 'Tem a certeza que pretende sair esta página?',
    leavePageSub: 'As alterações que realizou não serão gravadas.',
    stay: 'Ficar',
    leave: 'Sair',
  },

  navigation: {
    apps: 'Serviços',
    policies: 'Políticas de Autorização',
    history: 'Pedidos de Serviço',
    stats: 'Estatísticas',
    my_idp: 'A Minha Instituição',
    invite_request: 'Convite',
  },

  loader: {
    loading: 'A carregar todos os serviços',
  },

  facets: {
    title: 'Filtros',
    refresh: 'Atualizar',
    clear_all: '(Clear all)',
    reset: 'Limpar',
    download: 'Exportar',
    unknown: 'Desconhecido',
    totals: {
      all: 'A visualizar {{total}} serviços',
      filtered: 'A visualizar {{count}} de {{total}} serviços',
    },
    static: {
      connection: {
        all: 'Todos',
        has_connection: 'Sim',
        name: 'Serviço ligado',
        no_connection: 'Não',
      },
      license: {
        has_license_sp: 'Sim, fornecida pelo SP',
        has_license_surfmarket: 'Sim, fornecida pela SURFmarket',
        name: 'Licença',
        not_needed: 'Não é necessário',
        unknown: 'Não definida',
      },
      used_by_idp: {
        all: 'Todos',
        name: 'Fornecidos pela minha instituição',
        no: 'Não',
        yes: 'Sim',
      },
      published_edugain: {
        all: 'Todos',
        name: 'Publicados na federação eduGAIN',
        no: 'Não',
        yes: 'Sim',
      },
      interfed_source: {
        tooltip:
          'Alguns serviços disponíveis através da SURFconext são fornecidos através de outras federações que não a SURFconext. Aqui pode filtrar por federação.',
        name: 'Federação de origem',
        surfconext: 'SURFconext',
        edugain: 'eduGAIN',
        entree: 'Entrada',
      },
      entity_category: {
        name: 'eduGAIN Categorias de Entidade',
        tooltip:
          'Os serviços podem obedecer a \'categorias de entidade\'.<br>Consulte a <a href="https://wiki.surfnet.nl/display/surfconextdev/Entity+categories" target="_blank" rel="noopener noreferrer">wiki</a> para mais informações. Aqui pode filtrar os serviços que aderem a uma determinada categoria.',
        code_of_conduct: 'Código de Conduta',
        research_and_scholarship: 'Pesquisa e Bolsas de Estudo',
        selectAll: 'Filter on all',
        tooltipAll:
          'Check this to filter on services that<br>have all the checked entity categories.<br><br>The default unchecked behaviour is to<br>filter the services on one or more<br>checked entity categories.',
      },
      strong_authentication: {
        name: 'Suporta SURFsecureID',
        tooltip:
          'SURFsecureID second factor authentication is required.<br>For more information see the <a href="https://edu.nl/p4um4" target="_blank" rel="noopener noreferrer">wiki</a>.',
        none: 'None',
      },
      attribute_manipulation: {
        name: 'Script de manipulação de atributos personalizado',
        yes: 'Sim',
        no: 'Não',
      },
      arp: {
        name: 'Libertar atributos',
        info_html: 'É possível libertar mais atributos para o Serviço através da manipulação de atributos.',
      },
      type_consent: {
        tooltip:
          'Como é solicitado o consentimento aos novos utilizadores antes de aceder ao serviço. <br>Consulte a <a target="_blank" rel="noopener noreferrer" href="https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm">wiki</a> para mais informações.',
        name: 'Tipo de consentimento',
        no_consent: 'Sem consentimento',
        minimal_consent: 'Consentimento mínimo',
        default_consent: 'Consentimento por omissão',
      },
    },
  },

  apps: {
    tabs: {
      //TODO: translate
      about: 'About',
      attributes: 'Attributes & Privacy',
      resource_servers: 'Resource Servers',
      statistics: 'Statistics',
      settings: 'Settings',
    },
    detail: {
      about: 'About this service', //TODO: translate
      application_usage: 'Utilização do serviço',
      attribute_policy: 'Atributos',
      close_screenshot: 'Fechar',
      how_to_connect: 'Activar serviço',
      how_to_disconnect: 'Desativar serviço',
      idp_usage: 'Utilizado por',
      license_data: 'Licença',
      links: 'Links', // TODO: translate
      overview: 'Visão geral',
      connected_resource_servers: 'Resource servers',
      sirtfi_security: 'Sirtfi Security',
      ssid: 'SURF Secure ID',
      privacy: 'Privacidade',
      consent: 'Consentimento',
      back: 'Voltar',
      outstandingIssue:
        'Já existe um pedido de serviço excepcional {{jiraKey}} do tipo {{type}} e estado {{status}} para este Serviço.',
      inviteAlreadyProcessed: 'O convite para o pedido de serviço {{jiraKey}} já foi {{action}}.',
      outstandingIssueLink:
        ' Aceda à seção <a class="link" href="{{link}}">{{linkName}}</a>  para aprovar / recusar o convite.',
      approved: 'aprovado',
      denied: 'recusado',
      // TODO: translate
      institutions_header: {
        one: 'Used by 1 institution',
        other: 'Used by {{count}} institutions',
        zero: 'Used by no institutions',
      },
      institutions: 'Institutions',
      provided_information: '{{organisation}} provided the following information',
      connect_service: 'Connect this service',
      disconnect_service: 'Disconnect this service',
      connected: 'Connected',
      pending_connection: 'Pending connection request...',
      pending_disconnect: 'Pending disconnect request...',
      approve_invite: 'Approve invite',
      deny_invite: 'Deny invite',
    },
    overview: {
      connect: '',
      connect_button: 'Ativar',
      connected: 'Ativado',
      connected_services: 'Connected services', // TODO: translate
      all_services: 'All services', // TODO: translate
      dashboardConnectOption: 'Automatic connection', // TODO: translate
      license: 'Licença protegida',
      licenseStatus: 'Licença exigida',
      aansluitovereenkomstRefused: 'Política assinada',
      contractualBase: 'Contractual Base',
      license_present: {
        na: 'n/a',
        no: 'Não',
        unknown: 'Desconhecida',
        yes: 'Sim',
      },
      license_unknown: 'Desconhecida',
      name: 'Serviço',
      organisation: 'Vendor',
      no_results: 'Não existem serviços disponíveis',
      processing_results: 'A disponibilizar todos os serviços...',
      search: 'Pesquisar',
      search_hint: 'Filtrar por nome',
      add_services_hint:
        'Não foi encontrado o serviço que procura? Recorra ao endereço <a href="http://support.surfconext.nl/getconexted" target="_blank" rel="noopener noreferrer">SSO using SURFconext</a> e indique-nos qual o serviço que pretende utilizar. Com isto, o acesso ao serviço será realizado com a sua conta institucional, sendo mais seguro e eficiente para todas as partes envolvidas. Desta forma torna o serviço ainda mais atraente para outras instituições, dentro e fora dos Países Baixos. É aconselhado que seja feita uma reflecção sobre o tema e que seja enviado um email para support@surfconext.nl a solicitar um contato para que seja discutida a ligação do serviço.',
    },
    // TODO: translate
    settings: {
      title: 'Service settings',
      menu: {
        consent: 'Consent',
        authorization_policy: 'Authorization policy',
        surf_secure_id: 'SURF Secure ID',
      },
    },
  },

  app_meta: {
    question: 'Tem alguma questão?',
    eula: 'Termos & Condições',
    website: 'Website',
    support: 'Página de suporte',
    login: 'Página de entrada',
    registration_info_html:
      'Este fornecedor de serviço está disponivel na SURFconext através do <a href="https://support.surfconext.nl/edugain" target="_blank" rel="noopener noreferrer">eduGAIN</a>. O Fornecedor de Serviço é registado através da seguinte federação: <a href="{{url}}" target="_blank" rel="noopener noreferrer">{{url}}</a>.',
    registration_policy: 'Política de registo',
    privacy_statement: 'Política de Privacidade',
    metadata_link: 'Metadata',
  },
  license_info_panel: {
    title: 'Informação da Licença',
    has_license_surfmarket_html:
      'Existe uma licença válida através da <a href="https://www.surfmarket.nl" target="_blank" rel="noopener noreferrer">SURFmarket</a>.',
    has_license_sp_html:
      'A licença para <a href="{{serviceUrl}}" target="_blank" rel="noopener noreferrer">{{serviceName}}</a> pode ser adquirida ao fornecedor {{organisation}} deste serviço.',
    has_license_sp_html_no_service_url:
      'A licença para {{serviceName}} pode ser adquirida ao fornecedor {{organisation}} deste serviço.',
    no_license_html:
      'A sua instituição não tem uma licença válida disponível através da <a href="https://www.surfmarket.nl" target="_blank" rel="noopener noreferrer">SURFmarket</a>.',
    not_needed_html: 'Este Serviço não requer licença.',
    unknown_license: 'É desconhecido se é necessário licença.',
    no_license_description_html:
      '' +
      '<ul>' +
      '   <li>A sua instituição pode obter uma linceça a partir da <a href="https://www.surfmarket.nl" target="_blank" rel="noopener noreferrer">SURFmarket</a>.</li>' +
      '</ul>' +
      '<br />Em alguns casos a licença necessita de ser obtida directamente do fornecedor de serviço.',
    unknown_license_description_html:
      'Podem existir várias razões:' +
      '<ul>' +
      '   <li>A SURFnet ou outra instituição está a diponibilizar o serviço de forma gratuita.</li>' +
      '   <li>A licença necessita de ser obtida directamente do fornecedor de serviço.</li>' +
      '   <li>A licença não foi adicionada à administração da <a href="https://www.surfmarket.nl" target="_blank" rel="noopener noreferrer">SURFmarket</a>.</li>' +
      '</ul>' +
      '<p>Se necessário, a SURFnet pode contactar o fornecedor de serviço ou a <a href="https://www.surfmarket.nl" target="_blank" rel="noopener noreferrer">SURFmarket</a> antes de ativar a ligação.</p>',
  },

  license_info: {
    unknown_license: 'Nenhuma informação de licença disponível',
    has_license_surfmarket: 'Licença disponível através da SURFmarket',
    has_license_sp: 'Licença necessária (através do fornecedor de serviço)',
    no_license: 'Nenhuma licença disponível',
    no_license_needed: 'Não é necessário licença',
    license_info: 'Saiba como obter a licença',
    license_unknown_info: 'Ler mais',
    valid: 'Licença válida até {{date}}',
  },

  overview_panel: {
    entityID: 'Entity ID',
    rpClientID: 'Client ID',
    wiki_info_html:
      'Informações extras estão disponíveis para este serviço na SURFconext <a href="{{link}}" target="_blank" rel="noopener noreferrer">wiki</a>.',
    no_description: 'A descrição deste serviço não está disponível.',
    description: 'Descrição',
    has_connection: 'Ligações ativas',
    no_connection: 'Ligações inativas',
    how_to_connect: 'Saiba como ativar',
    disconnect: 'Saiba como desativar a ligação',
    normen_kader: 'Informação sobre o AVG/GDPR',
    normen_kader_html:
      'Para este serviço, foi publicada informação sobre quais os dados processados e onde processam esses dados. Pode encontrar esta informação na <a href="https://wiki.surfnet.nl/pages/viewpage.action?pageId=60689334" target="_blank" rel="noopener noreferrer">wiki</a>. Durante 2018 esta informação será incorporada na nova versão do Dashboard.',
    no_normen_kader_html:
      'Para este serviço, ainda não foi disponibilizada informação sobre o AVG/GDPR; informação sobre quais os dados que processam e onde podem ser solicitados ao fornecedor do serviço.',
    single_tenant_service: 'Serviço single tenant',
    single_tenant_service_html:
      '{{name}} é um serviço single tenant, como tal, requer uma instância separada para cada instituição que deseja ligar-se a este serviço. Para obter mais informações sobre serviços single tenant, consulte <a href="https://wiki.surfnet.nl/display/services/(Cloud)services" target="_blank" rel="noopener noreferrer">SURFnet wiki</a>',
    interfed_source: 'Origem da federação',
    publish_in_edugain_date: 'Publicado no eduGAIN em:',
    supports_ssa: 'SURFsecureID enabled',
    minimalLoaLevel:
      'For logging in to this service, second factor authentication is required via SURFsecureID. All users are required to use a token with at least the following Level of Assurance (LoA): <code>{{minimalLoaLevel}}</code>. For more information see the <a href="https://edu.nl/8nm6h" target="_blank" rel="noopener noreferrer">wiki</a>.',
    minimalLoaLevelIdp:
      'For logging in to this service, second factor authentication is required via SURFsecureID. All users from your institution are required to use a token with at least the following Level of Assurance (LoA): <code>{{minimalLoaLevel}}</code>. For more information see the <a href="https://edu.nl/8nm6h" target="_blank" rel="noopener noreferrer">wiki</a>.',
    entity_categories: 'Suporta Categorias de Identidade',
    entity_category: {
      'http://wwwgeantnet/uri/dataprotection-code-of-conduct/v1': 'GÉANT Data Protection Code of Conduct',
      'http://refedsorg/category/research-and-scholarship': 'Research and Scholarship',
      'http://clarineu/category/clarin-member': 'Clarin member',
    },
    aansluitovereenkomst: 'Protocolo de Adesão',
    aansluitovereenkomstRefused:
      '{{organisation}} recusou-se a assinar \'Protocolo de adesão à SURFconext\' com a SURF. Leia mais sobre esta política em <a href="https://wiki.surfnet.nl/display/surfconextdev/Afspraken+-+contracten+-+trustframework" target="_blank" rel="noopener noreferrer">SURF wiki</a>.',
    vendorInfo: 'This service is offered by {{organisation}}. ',
    privacyInformation: 'Informação de privacidade',
    privacyInformationInfo: '{{organisation}} não disponibilizou informação de privacidade.',
    contractualBase: {
      na: 'No info on contractual basis available: for any questions, please contact support@surfconext.nl.',
      ao: '{{organisation}} has signed the SURFconext connection agreement.',
      ix: 'Service offered by SURFconext member institution.',
      'r&s+coco':
        'eduGAIN service agreed to de Data Protection Code of Conduct and belongs to the Research & Scholarship category.',
      entree: 'Member of the Kennisnet Entree-federation.',
      clarin: 'Member of the Clarin research federation.',
    },
    contractualBaseWiki:
      ' For more information see the <a href="https://edu.nl/c83yx" target="_blank" rel="noopener noreferrer">wiki</a>.',
  },
  attributes_policy_panel: {
    arp: {
      noarp: "Não existe uma 'Política de Atributos' específica. Todos os atributos são enviados.",
      noattr: 'Não são enviados atributos para {{name}}.',
      manipulation:
        "Para este serviço existe efetivamente um 'script de manipulação de atributos'. A SURFconext executa o script para cada utilizador autenticado antes de enviar os atributos para o serviço. Para que possa compreender qual a informação a ser enviada, consulte em baixo uma descrição sobre o tratamento realizado pelo script:",
      resourceServers:
        'This Service is connected to Resource Servers and therefore all the attributes released are also accessible to the following Resource Servers:',
    },
    attribute: 'Atributo',
    hint: 'Os atributos e os respetivos valores para a sua conta pessoal são apresentados. Isto pode não ser representativo para outras contas na sua organização.',
    subtitle: '{{name}} quer receber os seguintes atributos',
    title: 'Atributos',
    your_value: 'O seu valor',
    filter: 'Para este atributo foram aplicados os seguintes filtros:',
    motivationInfo:
      'A coluna ‘motivação‘ contém na medida do possível, a explicação do fornecedor que o leva a precisar deste atributo.',
    motivation: 'Motivação',
    no_attribute_value: '<não foi recebido nenhum valor>',
    attribute_value_generated: '<é gerado pelo SURFconext>',
    filterInfo:
      'Para minimizar os dados a transmitir da insituição para o serviço, a SURFconext por vezes filtra os valores dos atributos.',
    warning: 'Observações:',
    nameIdInfo:
      "The user’s identity is transmitted as a NameID element with the type '%{type}' - <a href='https://support.surfconext.nl/uids' target='_blank'>generated by SURFconext</a>",
  },
  connected_resource_servers_panel: {
    title: 'Connected Resource Servers',
    subtitle: "{{name}} is an OIDC Relying Party and is allowed to query the API's of the following Resource Servers",
    clientId: 'Client ID',
    name: 'Name',
    description: 'Description',
  },
  idp_usage_panel: {
    title: 'Usado por',
    subtitle: 'As seguintes instituições estão ligadas a {{name}}.',
    subtitle_none: 'Não existem instituições ligadas a {{name}}.',
    subtitle_single_tenant:
      'Quando pretendeder saber quais as instituições a usar {{name}} através da SURFconext, envie um email para suporte@rctsaai.pt.',
    institution: 'Instituição',
  },
  sirtfi_panel: {
    title: 'O contato Sirtfi para {{name}}',
    subtitle:
      'A Framework de Resposta a Incidentes de Segurança para Identidades Federadas <a href=" https://refeds.org/sirtfi" target="_blank" rel="noopener noreferrer">(Sirtfi) </a> tem como objectivo permitir a coordenação da resposta a incidentes entre organizações federadas. Esta framework de confiança inclui uma lista de asserções que uma organização pode atestar, de forma a poder declarar que está em conformidade com Sirtfi.',
    contactPersons: 'O contato Sirtfi para este serviço:',
    cp_name: 'Nome',
    cp_email: 'Email',
    cp_telephoneNumber: 'Número de telefone',
    cp_type: 'Tipo',
    cp_type_translate_technical: 'Técnico',
    cp_type_translate_administrative: 'Administrativo',
    cp_type_translate_help: 'Suporte',
    cp_type_translate_support: 'Suporte',
  },
  privacy_panel: {
    title: 'Informação de Privacidade',
    subtitle:
      'A SURF fornece aos novos Serviços a oportunidade de partilhar informação sobre as suas politícas de RGPD. Se disponível, encontra-se abaixo. Para qualquer informação em falta, contacte o fornecedor.',
    subtitle2: 'O fornecedor do serviço {{name}} forneceu a seguinte informação (caso existam):',
    question: 'Questão',
    answer: 'Resposta',
    accessData: 'QUEM PODE TER ACESSO AOS DADOS?',
    certification: 'PODE O FORNECEDOR DISPONIBILIZAR UM MEMORANDO A TERCEIROS?',
    certificationLocation: 'ONDE PODE UMA INSTITUIÇÃO ENCONTRAR/SOLICITAR O MESMO?',
    country: 'EM QUE PAÍS ESTÃO OS DADOS ARMAZENADOS?',
    otherInfo: 'OUTRAS INFORMAÇÕES DE PRIVACIDADE E SEGURANÇA DE DADOS',
    privacyPolicy: 'O FORNECEDOR PUBLICA UMA POLÍTICA DE PRIVACIDADE?',
    privacyPolicyUrl: 'QUAL O URL DA POLÍTICA DE PRIVACIDADE?',
    securityMeasures: 'QUAIS MEDIDAS DE SEGURANÇA ADOTADAS PELO FORNECEDOR?',
    snDpaWhyNot: 'SE NÃO, QUAIS OS ARTIGOS QUE TEM PROBLEMAS & PORQUÊ?',
    surfmarketDpaAgreement: 'O FORNECEDOR CONCORDA COM A DPA DA SURFMARKET?',
    surfnetDpaAgreement: 'O FORNECEDOR PRETENDE ASSINAR O SURF MODEL DPA?',
    whatData: 'QUE(TIPO)DADOS SÃO PROCESSADOS?',
    certificationValidFrom: 'CERTIFICAÇÃO VÁLIDA DE',
    certificationValidTo: 'CERTIFICAÇÃO VÁLIDA PARA',
    noInformation: 'Não foi fornecida informação pelo fornecedor',
  },
  consent_panel: {
    title: 'Consentimento de novos utilizadores',
    subtitle:
      'Aos novos utilizadores será pedida autorização para enviar informação de dados pessoais para este serviço.',
    subtitle2:
      'Nesta página pode configurar a forma como será pedido o consentimento aos utilizadores antes de ser enviado para {{name}}. Pode configurar para ignorar o consentimento, pedir consentimento minimo e adicionar uma mensagem de consentimento personalizada para os utilizadores deste serviço. As diferentes configurações de consentimento são explicadas <a target="_blank" rel="noopener noreferrer" href="https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm">nesta página da wiki</a>.',
    subtitle2Viewer:
      'Nesta página pode visualizar de que forma é pedido o consentimento aos utilizadores antes de ser enviado para {{name}}. As diferentes configurações de consentimento são explicadas <a target="_blank" rel="noopener noreferrer" href="https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm">nesta página da wiki</a>.',
    no_consent: 'Não é necessário consentimento',
    minimal_consent: 'É necessário consentimento mínimo',
    default_consent: 'Consentimento por omissão com uma mensagem personalizada opcional',
    consent_value: 'Tipo de consentimento necessário',
    consent_value_tooltip: 'O tipo de consentimento determina como e se é pedido consentimento ao utilizador.',
    explanationNl: 'Mensagem em Dutch',
    explanationNl_tooltip:
      'Esta mensagem será adicionada ao interface de consentimento em Dutch para novos utilizadores.',
    explanationEn: 'Mensagem em Inglês',
    explanationEn_tooltip:
      'Esta mensagem será adicionada ao interface de consentimento em Inglês para novos utilizadores.',
    explanationPt: 'Mensagem em Português',
    explanationPt_tooltip:
      'Esta mensagem será adicionada ao interface de consentimento em Português para novos utilizadores.',
    save: 'Guardar alterações',
    loa_level: 'SURFsecureID Level of Assurance (LoA)',
    defaultLoa: 'LoA 1: Password authentication through SURFconext at the users home IdP',
    loa2: 'LoA 2 (see the wiki for more info)',
    loa3: 'LoA 3 (see the wiki for more info)',
  },
  ssid_panel: {
    title: 'SURFsecureID',
    subtitle:
      'With <a href="https://wiki.surfnet.nl/display/SsID" target="_blank" rel="noopener noreferrer">SURFsecureID</a> you can better secure access to services with strong authentication. ',
    subtitle2:
      'A user logs in with username and password (the first factor) and SURFsecureID takes care of the second factor authentication like via a mobile app or USB key.',
    subtitle3:
      'By chosing a higher <a href="https://edu.nl/8nm6h" target="_blank" rel="noopener noreferrer">Level of Assurance (LoA)</a> you can add additional protection to your service by adding a second factor to the user\'s login.',
    highestLoaReached:
      'You already have the highest LoA setting. You can not request a lowel LoA in this form. Please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> if you want to lower the LoA for this service.',
    appHasLoaLevel:
      'You can not request a Loa setting for this service. This service already has a Loa setting configured to be applied for all institutions. ',
  },
  how_to_connect_panel: {
    accept: 'Confirmo que li os termos e condiçoes e que aceito os mesmos em nome da minha instituição.',
    accept_disconnect: 'Sim, concordo que {{app}} deixará de estar disponível para minha organização',
    attributes: 'atributos',
    attributes_policy: 'política de atributos',
    privacy_policy: 'política de privacidade',
    back_to_apps: 'Voltar a todos os serviços',
    cancel: 'Cancelar',
    close: 'Close', //TODO: translate
    check: 'Verificar que',
    checklist: 'Finalize a lista de verificações antes de activar a ligação:',
    processing_agreements:
      'Verifique se a sua instituição necessita de um <a href="https://wiki.surfnet.nl/display/surfconextdev/Data+processing+agreement" target="_blank" rel="noopener noreferrer">contrato de adesão</a> para este serviço, se sim, se o mesmo já está assinado.',
    comments_description: 'Comentários serão enviados à SURFconext.',
    comments_placeholder: 'Adicione aqui os seus comentários...',
    comments_title: 'Comentários adicionais?',
    automatic_connect: 'Activate connection immediately', // TODO: translate
    connect: 'Ativar serviço',
    connect_title: 'Ligar {{app}}',
    connect_invite_title: 'Aceitar convite para ligar {{app}}',
    disconnect: 'Desativar serviço',
    disconnect_title: 'Desativar ligação com {{app}}',
    done_disconnect_subtitle_html:
      'Será contactado sobre os próximos passos, necessários para finalizar esta desativação. Se tem outras questões antes de proceder à finalização, por favor contacte <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
    done_disconnect_subtitle_html_with_jira_html:
      'Será contactado sobre os próximos passos, necessários para finalizar esta desativação. Se tem outras questões antes de proceder à finalização, por favor contacte <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> e adicione o seguinte número do pedido de serviço no assunto da mensagem: {{jiraKey}}.',
    done_disconnect_title: 'Pedido de Desativação!',
    done_subtitle_html:
      'Será contactado sobre os próximos passos, necessários para finalizar esta ativação. Se tem outras questões antes de proceder à finalização, por favor contacte <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
    done_subtitle_with_jira_html:
      'Será contactado sobre os próximos passos, necessários para finalizar esta ativação. Se tem outras questões antes de proceder à finalização, por favor contacte <a href="mailto:support@surfconext.nl?subject=Questões sobre a ligação {{jiraKey}}">support@surfconext.nl</a> e adicione o seguinte número do pedido de serviço no assunto da mensagem: {{jiraKey}}.',
    done_title: 'Ligação é solicitada!',
    rejected_without_interaction_title: 'Connection failed!',
    rejected_without_interaction_subtitle: 'Something went wrong while connecting.', // TODO: change text
    done_without_interaction_title: 'Connection established!', // TODO: check text. translate if text is sufficient
    done_without_interaction_subtitle: 'You can make use of it now.', // TODO: check text. translate if text is sufficient
    forward_permission: {
      after: ' para {{app}}.',
      before: 'SURFnet tem permissões para encaminhar ',
    },
    info_connection_without_interaction:
      'This service provider allows institutions to connect immediately. There is no need to wait for this request to be processed, you can use the service right away!', // TODO: translate
    info_connection_share_institution:
      'This service provider is a service offered by your Institution and therefore the connection can be made directly: you can use the service immediately!',
    info_sub_title:
      'Pode ativar uma ligação neste Dashboard. Recomendamos que verifique a lista de verificações e analise a informação específica desta aplicação antes de a ativar.',
    info_sub_invite_title:
      'Pode aceitar o convite para ativar uma ligação. Recomendamos que verifique a lista de verificações e analise a informação específica desta aplicação antes de a ativar.',
    info_title: 'Ativar ligação',
    jira_unreachable: 'Algo falhou com o seu pedido',
    jira_unreachable_description: 'Não é possivel realizar um pedido neste momento. Tente novamente mais tarde.',
    license: 'licença',
    license_info: 'informação de licença',
    obtain_license: {
      after: ' para usar {{app}}.',
      before: 'É da responsabilidade da minha instituição obter um ',
    },
    provide_attributes: {
      after: '.',
      before: 'É da responsabilidade da minha instituição fornecer as informações corretas ',
    },
    read: 'Ler o',
    single_tenant_service_warning:
      "Pedidos para ativar serviços 'single tenant' podem levar mais tempo a ser processados. A SURFnet entrará em contato para discutir o processo de ativação após receber a sua solicitação.",
    terms_title: 'Ao solicitar uma ativação está a aceitar os termos e condições',
    wiki: 'wiki para este serviço',
    aansluitovereenkomst_accept:
      "Certifico que concordo com a ligação a um serviço que não tenha assinado o 'SURFconext aansluitovereenkomst'.",
    not_published_in_edugain_idp: 'serviço eduGAIN',
    not_published_in_edugain_idp_info:
      "O serviço {{name}} não pode ser ligado porque a sua instituição não está no eduGAIN. Para aderir ao eduGAIN, por favor seleccione a opção 'Publicar no eduGAIN' na tab 'A minha Instituição' e faça um pedido de alteração.",
    edit_my_idp_link: "Criar solicitação de mudança em 'A minha instituição'",
    disconnect_jira_info:
      'Se pretende receber mais informações sobre o progresso deste pedido, por favor contacte <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> e adicione o número do pedido de serviço no assunto: {{jiraKey}}',
    invite_denied: 'Pedido de Serviço {{jiraKey}} atualizado com sucesso.',
    invite_accepted: 'Pedido de Serviço {{jiraKey}} foi atualizado com sucesso e com a sua aprovação.',
    deny: 'Recusar o convite',
    approve: 'Aprovar o convite',
    deny_invitation: 'Tem a certeza que não pretende aceitar o convite para se ligar {{app}}',
    deny_invitation_info:
      'Depois de recusar o convite de ligação, pode sempre voltar a realizar um pedido para ativar o serviço aqui no Dashboard.',
    invite_action_collision_title: 'O serviço {{app}} já está ligado.',
    invite_action_collision_subtitle: 'Mid-air colisão detectada.',
    invite_action_collision:
      'O convite {{app}} já foi aceite. Talvez alguem já tenha aceite o convite? Se tem alguma questão contacte <a href="mailto:support@surfconext.nl?subject={{jiraKey}}">support@surfconext.nl</a> e adicione o número do pedido no assunto do email: {{jiraKey}}.',
    test_connected_no_connection_title: 'Service {{app}} can not be connected.',
    test_connected_no_connection_subtitle:
      'The status of your institution is staging and therefore no services can connect.',
    test_connected_no_connection:
      'If you want to change the status of your institution please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
    activate_with_email: {
      title: 'Contact at institution for this service',
      subTitle:
        'In case the service provider wants to contact someone at your institution about this connection, who can they contact?',
      emailPlaceholder: 'Contact person at your institution',
      invalidEmail: 'Invalid email',
      disclaimer: 'I opt not to share any name with the service provider',
    },
  },
  application_usage_panel: {
    title: 'Utilização do serviço',
    download: 'Exportar',
    error_html:
      'As estatísticas estão indisponíveis de momento. <a href="mailto:support@surfconext.nl">Contate o suporte</a> para mais informações.',
  },

  contact: {
    email: 'E-mail de suporte do serviço',
  },
  export: {
    downloadCSV: 'Download como CSV',
    downloadPNG: 'Download como PNG',
    downloadPDF: 'Download como PDF',
  },
  search_user: {
    switch_identity: 'Trocar Identidade',
    search: 'Pesquisar',
    search_hint: 'Filtrar por nome',
    name: 'Nome',
    switch_to: 'Trocar para o perfil',
    switch: {
      role_dashboard_viewer: 'Viewer',
      role_dashboard_admin: 'Admin',
    },
  },

  not_found: {
    title: 'OOPS, I currently can’t show you that page.',
    subTitle: 'This can be due to, and may be fixed by:',
    reasonLoginPre: 'You’re trying to access a page where you need to login for. Please press ',
    reasonLoginPost: ' and see if that takes you to the page you tried to access.',
    reasonHelp:
      'You don’t have the right authorisation to access that URL. Please check the Help pages <a href="https://edu.nl/p4um4" target="_blank" rel="noopener noreferrer">Help</a> to see who should be able to access what.',
    reasonRemoved: 'The URL you tried to access does not exist (anymore). Sorry.',
    reasonUnknown:
      'You ran into something else, need help and/or maybe we have to fix this. Send us a mail at <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> so we can have a look.',
  },

  server_error: {
    title: 'Não tem permissões suficientes para aceder ao Dashboard.',
    description_html: 'Contacte <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
  },

  logout: {
    title: 'Logout concluído com sucesso.',
    description_html: '<strong>TEM</strong> de fechar o browser para finalizar o processo de logout.',
  },

  footer: {
    tips_or_info: 'Precisa de dicas ou informações?',
    help_html:
      '<a href="https://wikurfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HandleidingSURFconextDashboard" target="_blank" rel="noopener noreferrer">Ajuda</a>',
    surf_html: '<a href="https://www.surf.nl/en" target="_blank" rel="noopener noreferrer">SURF</a>',
    terms_html:
      '<a href="https://support.surfconext.nl/terms-en" target="_blank" rel="noopener noreferrer">Termos do Serviço</a>',
    contact_html: '<a href="mailto:support@surfconext.nl">support@surfconext.nl</a>',
  },

  my_idp: {
    title: 'A minha instituição',
    roles: 'Perfis',
    sub_title_html:
      'Os seguintes perfis foram atribuídos (<a target="_blank" rel="noopener noreferrer" href="https://wiki.surfnet.nl/display/surfconextdev/Rolverdeling+contactpersonen">mais info</a>):',
    role: 'Perfil',
    users: 'Utilizador(es)',
    settings: 'Configurações da minha Instituição',
    settings_edit: 'Configurações da minha instituição e serviços',
    settings_text:
      "Esta secção contém várias configurações da sua instituição e de Fornecedores de Serviço(s) fornecidos à RCTSaai através da sua instituição.Estas configurações são utilizadas na RCTSaai, por exemplo na página Where Are You From. Se pretende alterar informação, clique em 'Criar pedido de alteração'.",
    settings_text_viewer:
      'Esta secção contém várias configurações da sua instituição e de Fornecedores de Serviço(s) fornecidos à RCTSaai através da sua instituição.Estas configurações são utilizadas na RCTSaai, por exemplo na página de onde é.',
    SURFconextverantwoordelijke: 'SURFconext owner',
    SURFconextbeheerder: 'SURFconext maintainer',
    'Dashboard supergebruiker': 'Dashboard Super Utilizador',
    services_title: 'Serviços fornecidos pela sua instituição:',
    services_title_none: 'Nenhum',
    service_name: 'Nome do Serviço',
    license_contact_html:
      'Contacto Primário da Licença (<a target="_blank" rel="noopener noreferrer" href="https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?">mais info</a>):',
    license_contact_name: 'Nome',
    license_contact_email: 'Email',
    license_contact_phone: 'Número de Telefone',
    institution: 'Instituição',
    services: 'Serviços',
    edit: 'Criar pedido de alteração',
    entity_id: 'ID de Identidade',
    state: 'Estado',
    prodaccepted: 'Produção',
    testaccepted: 'Testes',
    all: 'Todos',
    name: {
      en: 'Nome (en)',
      nl: 'Nome (nl)',
      pt: 'Nome (pt)',
    },
    displayName: {
      en: 'Nome de Exibido (en)',
      nl: 'Nome de Exibido (nl)',
      pt: 'Nome de Exibido (pt)',
    },
    organizationURL: {
      en: 'URL da Organização (en)',
      nl: 'URL da Organização (nl)',
      pt: 'URL da Organização (pt)',
    },
    organizationURL_nl_tooltip:
      'URL onde o utilizador final pode aceder para obter mais informações sobre a organização em Dutch.',
    organizationURL_en_tooltip:
      'URL onde o utilizador final pode aceder para obter mais informações sobre a organização em Inglês.',
    organizationURL_pt_tooltip:
      'URL onde o utilizador final pode aceder para obter mais informações sobre a organização em Português.',
    organizationName: {
      en: 'Nome da Organização (en)',
      nl: 'Nome da Organização (nl)',
      pt: 'Nome da Organização (pt)',
    },
    organizationName_nl_tooltip: 'Nome oficial da organização em Dutch.',
    organizationName_en_tooltip: 'Nome oficial da organização em Inglês.',
    organizationName_pt_tooltip: 'Nome oficial da organização em Português.',
    organizationDisplayName: {
      en: 'Nome de Apresentação da Organização(en)',
      nl: 'Nome de Apresentação da Organização(nl)',
      pt: 'Nome de Apresentação da Organização(pt)',
    },
    organizationDisplayName_nl_tooltip: 'Nome de visualização oficial da organização em Dutch.',
    organizationDisplayName_en_tooltip: 'Nome de visualização oficial da organização em Inglês.',
    organizationDisplayName_pt_tooltip: 'Nome de visualização oficial da organização em Português.',
    keywords: {
      en: 'Keywords (en)',
      nl: 'Keywords (nl)',
      pt: 'Keywords (pt)',
    },
    published_in_edugain: 'Publicado no eduGAIN',
    date_published_in_edugain: 'Data de publicação no eduGAIN',
    logo_url: 'Logo',
    new_logo_url: 'URL do Novo logo ',
    research_and_scholarship_info: "Ligar-se automáticamente aos SP's compatíveis com a categoria CoCo R&S",
    research_and_scholarship_tooltip:
      'Isto significa que o vosso IdP ativa automáticamente ligações para todos os SPs na <br>SURFconext que aderiram à categoria ‘Research & Scholarship Entity Category’<br> e ‘GEANT Data Protection Code of Conduct’, libertanto os atributos R&S. <br>Consulte<a href="https://wiki.surfnet.nl/pages/viewpage.action?pageId=86769882" target="_blank" rel="noopener noreferrer">wiki</a> para mais informações.',
    allow_maintainers_to_manage_authz_rules: 'Allow maintainers to manage Authorization rules',
    allow_maintainers_to_manage_authz_rules_tooltip:
      'This means the maintainers of your<br>IdP are allow to create / edit and delete<br>Authorization rules.',
    displayAdminEmailsInDashboard: 'Allow members to see admin emails',
    displayAdminEmailsInDashboardTooltip:
      'This means that regular members of your institution<br>can see the emails of the SURFconext maintainers<br>and owners of this institution.',
    displayStatsInDashboard: 'Allow regular members to see statistics',
    displayStatsInDashboardTooltip:
      'This means that regular members of your institution<br>can see the usage / statistics of the connected services<br>to this institution.',
    contact: 'Contacto para {{name}}',
    contact_name: {
      title: 'Nome do contacto',
    },
    contact_email: {
      title: 'Email do contacto',
      tooltip:
        "Attention: you're advised to use a functional email address (admin@.. tech@... helpdesk@... ) which doesn't change when someone leaves your institution.", // TODO: translate
    },
    contact_type: {
      title: 'Tipo de contacto',
    },
    contact_telephone: {
      title: 'Telefone do contacto',
    },
    contact_types: {
      technical: {
        title: 'Técnico',
        display: 'Técnico',
        tooltip:
          'The technical contact person of the IdP. First contact for down times, changes, and other technical affairs.', // TODO: translate
        alttooltip: 'suggestion: technical person for down times and changes.', // TODO: translate
      },
      support: {
        title: 'Suporte',
        display: 'Suporte',
        tooltip:
          'This address is referred to when end users are having difficulty logging in. Generally this is the help desk of the IdP.', // TODO: translate
        alttooltip: 'suggestion: service desk of the institution.', // TODO: translate
      },
      help: {
        title: 'Suporte',
        display: 'Suporte',
        tooltip:
          'This address is referred to when end users are having difficulty logging in. Generally this is the help desk of the IdP.', // TODO: translate
        alttooltip: 'suggestion: service desk of the institution.', // TODO: translate
      },
      administrative: {
        title: 'Administrativo',
        display: 'Administrativo',
        tooltip:
          "The administrative contact person of the IdP. This tends to be the person filling the role of 'SURFconext-verantwoordelijke'", // TODO: translate
        alttooltip: "suggestion: person with the role 'SURFconext-verantwoordelijke'", // TODO: translate
      },
      other: {
        title: 'Other:<br>',
        display: 'Other',
        tooltip: 'Unclassified other contact person<br><br>',
        alttooltip: 'suggestion: None',
      },
      billing: {
        title: 'Billing:<br>',
        display: 'Billing',
        tooltip:
          'The billing contact person of the IdP. This tends to be the person of the financial department<br><br>',
        alttooltip: 'suggestion: financial department of the institution',
      },
    },
    description: {
      en: 'Descrição (en)',
      nl: 'Descrição (nl)',
      pt: 'Descrição (pt)',
    },
    guest_enabled: 'Acesso a convidados ativo',
    guest_enabled_tooltip:
      'When enabled this means users of the Guest IdP are<br>allowed to connect to this service.<br>See the <a href="https://edu.nl/46yyn" target="_blank" rel="noopener noreferrer">wiki</a> for more information.',
    edit_message: 'Pode editar os seguintes campos.',
    save: 'Criar pedido de alteração',
    change_request_created: 'Pedido de alteração enviado para a SURFnet SURFconext-team.',
    no_change_request_created: 'Não foi criado pedido de alteração já que não existem alterações.',
    change_request_failed: 'Falha ao criar o seu pedido de alteração.',
    comments: 'Comentários',
  },

  policies: {
    confirmation: "Tem a certeza que pretende remover a politíca '{{policyName}}?'",
    flash: "Política de autorização '{{policyName}}' foi {{action}} com sucesso",
    flash_created: 'criada',
    flash_deleted: 'removida',
    flash_first:
      'Esta é a primeira política de autorização para este serviço. Antes de ficar ativo, a equipa SURFconext tem de realizar alterações de configuração manualmente. Foi enviada uma notificação para a equipa SURFconext. Em breve será contactado.',
    flash_updated: 'atualizado',
    new_policy: 'Nova Política de Autorização',
    no_policies: 'There are no policies for this service.',
    how_to: 'Ajuda',
    policy_name_not_unique_exception: 'Este nome de política já está a ser utilizado',
    pdp_unreachable: 'PDP inacessível',
    pdp_unreachable_description:
      'De momento não é possível obter as políticas do PDP. Por favor, tente novamente mais tarde.',
    pdp_active_info: 'Click to read more about when your rule is active.',
    pdp_active_link: 'https://support.surfconext.nl/pdp-rule-active-after',
    overview: {
      active: 'Ativo',
      description: 'Descrição',
      header: 'Authorization policies',
      identityProviderNames: 'Instituições',
      inactive: 'Inactive',
      name: 'Nome',
      numberOfRevisions: 'Revisões',
      search: 'Pesquisar',
      search_hint: 'Filtrar por nome',
      serviceProviderName: 'Serviço',
    },
  },

  policy_attributes: {
    attribute: 'Atributo',
    attribute_value_placeholder: 'Valor do atributo...',
    group_info:
      " O valor(es) deve ser um ID de grupo totalmente qualificado ex. 'urn:collab:group:surfteams.nl:nl:surfnet:diensten:admins'",
    new_attribute: 'Adicionar novo atributo....',
    new_value: 'Adicionar novo valor...',
    sab_info: " O(s) valor(es) têm de corresponder a perfis válidos no SAB ex. 'Instellingsbevoegde'",
    values: 'Valor(es)',
    help_link: 'https://wiki.surfnet.nl/display/surfconextdev/Attributes+in+SURFconext',
    attributeTooltip: 'Click to read more about attributes.',
  },

  policy_detail: {
    //TODO: translate
    about: 'About',
    access: 'Acesso',
    access_denied_message: '"Access Denied" message',
    activate_policy: 'Activate this policy',
    deactivate_policy: 'Deactivate this policy',
    attribute: 'Atributos',
    autoFormat: 'Formatação automática da descrição da política',
    cancel: 'Cancelar',
    confirmation: 'Tem a certeza que pretende sair desta página?',
    create_policy: 'Criar nova política de autorização',
    deny: 'Negar',
    deny_info:
      'Políticas para não permitir, são menos comuns de utilizar. Se os atributos na politíca corresponderem aos do utilizador que está a realizar um login, então o resultado será não permitir o acesso. Nenhuma correspondência resulta em permitir. ',
    deny_message: 'Não Permitir - Mensagem em Inglês',
    deny_message_info:
      'Esta é a mensagem disponibilizada ao utilizador se o acesso não for autorizado com base nesta política.',
    deny_message_nl: 'Não Permitir - Mensagem em Dutch',
    deny_message_pt: 'Não Permitir - Mensagem em Português',
    description: 'Descrição',
    idps_placeholder: 'Selecione os Fornecedores de Identidade - zero ou mais',
    institutions: 'Instituições',
    intro:
      'Define who can access this service. Need help? <a href="https://support.surfconext.nl/dashboard-help-pdp" target="_blank" rel="noopener noreferrer">Read our manual.</a>',
    isActive: 'Ativo',
    isActiveDescription: 'Marcar a política de permisão como ativa',
    isActiveInfo: ' Políticas de autorização inativas não são avaliadas nas decisões',
    name: 'Nome',
    permit: 'Permitir',
    permit_info:
      'Políticas para Permitir, garantem que apenas uma correspondência bem sucedida dos atributos definidos resultará em permitir o acesso. Nenhuma correspondência resulta em não permitir.',
    rule: 'Regra',
    rules: 'Rules',
    rule_and: 'AND',
    rule_and_info:
      'Políticas com regras AND garantem que todos os atributos definidos tem de corresponder aos da pessoa que está a tentar fazer login.',
    rule_info_add:
      ' Note que ao definir vários valores de atributo para um mesmo nome de atributo, estes serão sempre avaliados com lógica OR.',
    rule_info_add_2:
      'Note que uma política de acesso Não Autorizado utiliza sempre implicitamente a lógica de AND para diferentes nomes de atributos.',
    rule_or: 'OR',
    rule_or_info:
      'Políticas definidas com a lógica OR apenas obriga a que um dos atributos do utilizador faça correspondência, ao que está a ser pedido para o acesso.',
    save_changes: 'Save changes',
    service: 'Serviço',
    spScopeInfo: 'Os Serviços disponíveis são limitados aos seus serviços se não selecionar uma Instituição',
    sp_placeholder: 'Selecione o Fornecedor de Serviço - obrigatório',
    sub_title: 'Criado por {{displayName}} em {{created}}',
    submit: 'Submeter',
    update_policy: 'Atualizar política de autorização',
  },

  revisions: {
    active: 'Ativo',
    allAttributesMustMatch: 'Regra de lógica OR?',
    attributes: 'Atributos',
    changes_first_html:
      'Este é a primeira <span class="curr">revisão inicial {{currRevisionNbr}}</span> criada por {{userDisplayName}} de {{authenticatingAuthorityName}} em {{createdDate}}.',
    changes_info_html:
      'Alterações realizadas entre <span class="prev"> revisão número {{prevRevisionNbr}}</span> e <span class="curr">revisão número {{currRevisionNbr}}</span> realizada por {{userDisplayName}} de {{authenticatingAuthorityName}} em {{createdDate}}.',
    denyAdvice: 'Não Permitir - Mensagem em Inglês',
    denyAdviceNl: 'Não Permitir - Mensagem em Dutch',
    denyAdvicePt: 'Não Permitir - Mensagem em Português',
    denyRule: 'Regra para Permitir Acesso?',
    description: 'Descrição',
    identityProviderNames: 'Instituições',
    name: 'Nome',
    revision: 'Número da Revisão',
    serviceProviderName: 'Serviço',
    title: 'Revisões',
    intro_1:
      'Sempre que uma política é atualizada é efetuada uma cópia da política anterior como uma revisão da nova política. Ao comparar revisões de políticas entre si e com a política mais atual podemos apresentar um registo de auditoria de todas as alterações efetuadas a uma política.',
    intro_2:
      'Quando uma política é apagada, todas as revisões dessa política, no caso de existirem, serão também apagadas.',
  },

  history: {
    header: 'Tickets',
    filter: 'Filter',
    last_updated: 'Last updated:',
    info: 'Nesta página pode encontrar todos os pedidos de serviços relacionados com a (des)ativação de serviços e pedidos de alterações.',
    moreAwaitingTickets:
      "Not all 'Awaiting Input' tickets are shown because the period in the search filter is not broad enough.",
    requestDate: 'Criado',
    updateDate: 'Atualizado',
    type: 'Tipo',
    jiraKey: 'Id',
    status: 'Estado',
    message: 'Message',
    userName: 'Por',
    spName: 'Serviço',
    action_types_name: {
      LINKREQUEST: 'Nova Ligação',
      UNLINKREQUEST: 'Desativar',
      QUESTION: 'Questão',
      CHANGE: 'Pedido de alteração',
      LINKINVITE: 'Convite para ativar',
    },
    from: 'De',
    to: 'Para',
    typeIssue: 'Tipo',
    spEntityId: 'Serviço',
    statuses: {
      all: 'All tickets',
      'To Do': 'Abrir',
      'In Progress': 'Em progresso',
      'Awaiting Input': 'Input pendente',
      Resolved: 'Resolvido',
      Closed: 'Fechado',
      undefined: 'Indefinido',
    },
    resolution: {
      no_change_required: 'Não foram necessárias alterações',
      no_change_requiredTooltip: 'O pedido não necessitou de alteração.',
      incomplete: 'Incompleto',
      incompleteTooltip: 'O pedido está incompleto.',
      done: 'Efetuado',
      doneTooltip: 'O pedido está resolvido.',
      wont_do: 'Não será resolvido',
      wont_doTooltip: 'O pedido não será resolvido.',
      cancelled: 'Cancelado',
      cancelledTooltip:
        'O pedido foi cancelado.Se o pedido foi um convite para ligar um serviço, a Instituição não aceitou o convite.',
      wont_fix: 'Não será resolvido',
      wont_fixTooltip: 'O pedido não será resolvido.',
      resolved: 'Resolvido',
      resolvedTooltip: 'O pedido foi resolvido com sucesso.',
      duplicate: 'Duplicado',
      duplicateTooltip: 'O pedido era um duplicado de outro seviço.',
      not_completed: 'Incompleto',
      not_completedTooltip: 'O pedido está incompleto.',
      cannot_reproduce: 'Não foi possível reproduzir',
      cannot_reproduceTooltip: 'A situação descrita no pedido não foi possivel reproduzir',
      suspended: 'Suspenso',
      suspendedTooltip: 'O pedido foi suspenso.',
    },
    servicePlaceHolder: 'Pesquisa e selecione um serviço...',
    noTicketsFound: 'Não foram encontrados pedidos para o filtro fornecido.',
    viewInvitation: 'Aprovar / Recusar',
    serviceDetails: 'View service details',
  },

  stats: {
    filters: {
      name: 'Filtros',
      allServiceProviders: 'Todos os Serviços',
    },
    state: 'Estado',
    timeScale: 'Período',
    date: 'Data',
    from: 'De',
    to: 'Até e incluindo',
    today: 'Hoje',
    sp: 'Serviço',
    period: {
      year: 'Ano',
    },
    displayDetailPerSP: 'Visualizar detalhes por Serviço',
    scale: {
      year: 'Ano',
      quarter: 'Trimestre',
      month: 'Mês',
      week: 'Semana',
      day: 'Dia',
      hour: 'Hora',
      minute: 'Minutos',
      all: 'Período completo: de -> até',
    },
    helpLink:
      'https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-Statistieken',
  },
  chart: {
    title: 'Logins e utilizadores por dia',
    chart: 'Número de logins por {{scale}}',
    chartAll: 'Número de logins',
    userCount: 'Número total de logins',
    uniqueUserCount: 'Utilizadores únicos',
    loading: 'A processar logins....',
    noResults: 'Não foram realizados logins para o período definido.',
    date: 'Data',
    logins: 'Logins por {{scale}}',
    allLogins: '# Logins',
    uniqueLogins: 'Logins Únicos',
    sp: 'Serviço',
    idp: 'Instituição',
  },
  clipboard: {
    copied: 'Copiado!',
    copy: 'Copiar para o clipboard',
  },
  live: {
    chartTitle: 'Logins por {{scale}}',
    aggregatedChartTitlePeriod: 'Logins durante {{period}} por {{group}}',
    noTimeFrameChart: 'Logins de {{from}} até {{to}}',
  },
  service_filter: {
    title: 'Filtrar serviços',
    state: {
      tooltip: 'O estado do Service determina se o Serviço está visivel na platforma de produção.',
    },
    search: 'Pesquisar serviços...',
  },
  invite_request: {
    info: 'Um pedido de convite resulta num email enviado para todos os contactos da instituição com um convite para o serviço selecionado. Um pedido de serviço para <span class="emphasize">Convite de Ligação</span> é criado com o estado <span class="emphasize">À Espera de Entrada</span>.',
    selectIdp: 'Pesquisa e selecione uma Instituição...',
    selectSpDisabled: 'Primeiro selecione uma Instituição',
    selectSp: 'Agora pesquisar e selecione um Serviço...',
    idp: 'Instituição',
    sp: 'Serviço',
    contactPersons: 'Selecione os contactos de {{name}} para os quais pretende enviar o convite.',
    sourcePersons: 'Contactos de {{source}}',
    additionalPersons: 'Contactos adicionais',
    selectContact: 'Selecione',
    sendRequest: 'Submeter',
    reset: 'Reset',
    message: 'Uma mensagem - opcional - para os destinatários do convite.',
    jiraFlash:
      'Foi criado um pedido de serviço com a chave {{jiraKey}}.Quando um dos destinatários aceitar o convite, o mesmo será registado nos comentários de  {{jiraKey}}.',
    resend: 'Ticket was created at {{date}} and emails were send to {{emailTo}}. Status is {{status}}',
  },
  profile: {
    title: 'Perfil',
    sub_title:
      'Os seguintes dados de perfil foram fornecidos pela sua instituição. Esta informação assim como os grupos a que pertence (ex.:SURFteams) serão registados na SURFconext e partilhados com os serviços utilizados através da RCTSaai.',
    my_attributes: 'Os meus atributos',
    attribute: 'Atributos',
    value: 'Valores',
    my_roles: 'Os meus perfis',
    my_roles_description: 'Os seguintes perfis foram atibuídos:',
    role: 'Perfil',
    role_description: 'Descrição',
    roles: {
      ROLE_DASHBOARD_ADMIN: {
        name: 'SURFconext Administrador',
        description: 'Está autorizado pela sua instituição a realizar a gestão de ligações a serviços',
      },
      ROLE_DASHBOARD_VIEWER: {
        name: 'SURFconext Suporte',
        description: 'Está autorizado pela sua instituição a visualizar a informação sobre serviços',
      },
      ROLE_DASHBOARD_MEMBER: {
        name: 'Institution member',
        description: 'You are a regular member of the dashboard',
      },
      ROLE_DASHBOARD_SUPER_USER: {
        name: 'Dashboard Super User',
        description: 'Tem permissões de superuser no dashboard',
      },
    },
    attribute_map: {
      uid: {
        name: 'UID',
        description: 'o seu username único dentro da sua organização',
      },
      'Shib-surName': {
        name: 'Sobrenome',
        description: 'o seu sobrenome',
      },
      'Shib-givenName': {
        name: 'Nome',
        description: 'o seu nome',
      },
      'Shib-commonName': {
        name: 'Nome Completo',
        description: 'o seu nome completo',
      },
      displayName: {
        name: 'Nome a Apresentar',
        description: 'nome a apresentar nas aplicações',
      },
      'Shib-InetOrgPerson-mail': {
        name: 'Endereço de E-mail ',
        description: 'o seu endereço de e-mail conhecido na sua instituição',
      },
      'Shib-eduPersonAffiliation': {
        name: 'Afiliação',
        description: 'a(s) sua afiliação(s) com a organização',
      },
      'Shib-eduPersonScopedAffiliation': {
        name: 'Scoped relation',
        description: 'a(s) sua afiliação(s) com a organização concatenado com o @dominio da organização',
      },
      eduPersonEntitlement: {
        name: 'Entitlement',
        description: 'define um direito, utilizado pelas aplicações no processo de autorização',
      },
      'Shib-eduPersonPN': {
        name: 'Net-ID',
        description: 'o seu username único dentro da sua instituição concatenado com o @dominio da organização',
      },
      'Shib-preferredLanguage': {
        name: 'Idioma Preferencial',
        description: 'uma abreviação de duas letras de acordo com o ISO 639; em subcódigos',
      },
      schacHomeOrganization: {
        name: 'Organização',
        description: 'nome da organização, que faz uso do dominío em conformidade com o RFC 1035',
      },
      'Shib-schacHomeOrganizationType': {
        name: 'Tipo de Organização',
        description: 'tipo de organização a que o utilizador pertence',
      },
      'Shib-schacPersonalUniqueCode': {
        name: 'Código único pessoal',
        description: 'estes valores são usados para expressar tipos específicos de números de identificação',
      },
      'Shib-nlEduPersonHomeOrganization': {
        name: 'Nome de apresentação da Organização',
        description: 'nome de apresentação da organização',
      },
      'Shib-nlEduPersonOrgUnit': {
        name: 'Nome da Unidade Orgânica',
        description: 'nome da unidade orgânica',
      },
      'Shib-nlEduPersonStudyBranch': {
        name: 'Área de Estudo',
        description:
          'área de estudo;string númerica que contém o código CROHOcode. pode estar vazio se a área for desconhecida',
      },
      'Shib-nlStudielinkNummer': {
        name: 'Studielinknummer',
        description: 'studielinknummer do estudante registado em www.studielink.nl',
      },
      'Shib-nlDigitalAuthorIdentifier': {
        name: 'DAI',
        description: 'Digital Author Identifier (DAI)',
      },
      'Shib-userStatus': {
        name: 'Estado do Utilizador',
        description: 'Estado do utilizador na SURFconext',
      },
      'Shib-accountstatus': {
        name: 'Estado da Conta',
        description: 'Estado da conta na SURFconext',
      },
      'name-id': {
        name: 'Identificador',
        description: 'Identificador da conta na SURFconext',
      },
      'Shib-voName': {
        name: 'Nome da Organização Virtual',
        description: 'O nome da Organização Virtual para a qual autenticou',
      },
      'Shib-user': {
        name: 'Identificador',
        description: 'Identificador da conta na SURFconext',
      },
      'is-member-of': {
        name: 'Membro',
        description: 'Membro da Organização Virtual e SURFConext.',
      },
      'Shib-surfEckid': {
        name: 'SURF EDU-K',
        description: 'Educatieve Content Keten Identifier (ECK ID) is a pseudonymous identifier.',
      },
    },
  },
}
