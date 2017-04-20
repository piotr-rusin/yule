INSERT INTO articles
(title, slug, content, creation_date, modification_date, is_blog_post, status, publication_date)
VALUES
(
	'The first title',
	'the-first-slug',
	'Example content.',
	'2012-01-01',
	'2012-01-02',
	TRUE,
	'PUBLISHED',
	'2012-01-03'
),
(
	'Yet another title',
	'example-custom-slug',
	'More content, this time longer.',
	'2012-02-07',
	'2012-02-15',
	TRUE,
	'PUBLISHED',
	'2012-02-11'
),
(
	'First draft',
	'this-is-a-draft',
	'Content',
	'2012-03-01',
	'2012-03-06',
	TRUE,
	'DRAFT',
	NULL
),
(
	'Lorem ipsum',
	'lorem-ipsum',
	'Dolor sit amet',
	'2012-02-04',
	'2012-02-05',
	TRUE,
	'PUBLISHED',
	'2012-02-04'
),
(
	'Duis tincidunt laoreet neque',
	'duis-tincidunt-laoreet-neque',
	'Nulla facilisi. Nunc tortor ante, posuere vel enim id, dictum
	 ultricies lorem.',
	'2012-03-11',
	'2012-03-14',
	FALSE,
	'DRAFT',
	NULL
),
(
	'Proin vel massa molestie',
	'proin-vel-massa-molestie',
	'In euismod neque at dolor porta vulputate. Integer euismod ex
	 eu massa congue, in placerat enim porta.',
	'2012-02-07',
	'2012-02-11',
	TRUE,
	'PUBLISHED',
	'2012-02-10'
),
(
	'Maecenas id sollicitudin orci',
	'maecenas',
	'In posuere, diam at tincidunt ullamcorper, sapien purus
	 malesuada lorem, quis ullamcorper nibh risus in leo',
	'2012-04-01',
	'2012-05-16',
	FALSE,
	'PUBLISHED',
	'2012-04-15'
),
(
	'Morbi eget congue velit',
	'morbi-eget',
	'Aliquam sit amet felis sed est tempor finibus. Vestibulum ut
	 tincidunt turpis, in interdum ligula.',
	'2012-04-22',
	'2012-05-01',
	FALSE,
	'PUBLISHED',
	'2012-05-15'
),
(
	'Nulla lectus dui, feugiat ut nisi quis, faucibus imperdiet est',
	'nulla-lectus-dui',
	'Aenean imperdiet dignissim ex, quis placerat leo.
	 Vivamus consectetur non sem id fermentum. Aliquam eget
	 consequat lacus. Sed eget viverra ipsum, id volutpat mi.
	 Aliquam non viverra lacus, in semper magna.',
	'2012-06-14',
	'2012-06-18',
	TRUE,
	'DRAFT',
	NULL
),
(
	'Pellentesque in diam luctus, suscipit velit a, ultrices enim',
	'pellentesque-in-diam',
	'Suspendisse feugiat lectus mollis, congue ante sit amet,
	 vestibulum mi. Nullam faucibus, mauris nec fringilla laoreet,
	 metus erat lobortis massa, eu volutpat ante velit nec urna.
	 Sed lobortis varius lacus et mattis. Sed dictum feugiat augue
	 eu pellentesque.',
	'2012-07-01',
	NULL,
	TRUE,
	'DRAFT',
	NULL
),
(
	'Aenean elementum massa vel egestas cursus',
	'aenean-elementum-massa',
	'Pellentesque urna nisl, ornare sit amet convallis ut,
	 malesuada in justo. Vivamus porta justo commodo tempor
	 viverra. Duis in congue nunc, at malesuada felis. Donec
	 vestibulum rhoncus augue aliquet commodo. Vestibulum ante
	 ipsum primis in faucibus orci luctus et ultrices posuere
	 cubilia Curae; Sed luctus nulla sed est lobortis eleifend.
	 Nam ornare efficitur facilisis. In varius dictum accumsan.
	 Praesent eleifend finibus orci, ut finibus lorem tincidunt
	 venenatis.',
	'2012-09-21',
	'2012-09-24',
	TRUE,
	'PUBLISHED',
	'2012-09-22'
),
(
	'Vestibulum id fringilla erat',
	'vestibulum-id-fringilla',
	'In et facilisis sapien, ac sagittis mi. Ut pharetra tristique
	 enim, sed sollicitudin magna posuere ac. Etiam nec ullamcorper
	 urna. Etiam mi velit, maximus sed odio in, finibus semper
	 nibh. Quisque condimentum risus vitae lectus consectetur
	 venenatis iaculis venenatis nisl. Sed condimentum malesuada mi
	 eget malesuada. Sed vehicula diam pellentesque diam semper,
	 a dapibus nulla aliquam.',
	'2012-10-09',
	'2012-10-28',
	TRUE,
	'PUBLISHED',
	'2012-11-05'
),
(
	'Maecenas ac leo nulla',
	'maecenas-ac-leo-nulla',
	'Quisque pharetra hendrerit purus eu tempor. Suspendisse
	 potenti. Duis tempus eu magna finibus vestibulum. Fusce
	 porttitor ac urna eget venenatis. Suspendisse quis elit vel
	 nibh consequat fermentum non et arcu. Class aptent taciti
	 sociosqu ad litora torquent per conubia nostra, per inceptos
	 himenaeos. Morbi sit amet luctus elit, non ornare nibh. Aenean
	 nisl dui, interdum varius erat ut, laoreet convallis ante.
	 Nulla sollicitudin ullamcorper sodales. Cras consectetur eros
	 aliquam, rhoncus lorem laoreet, finibus dui. Duis scelerisque
	 tempor ante, ac iaculis tellus sodales vel.',
	'2012-12-02',
	'2012-12-28',
	FALSE,
	'PUBLISHED',
	'2012-12-08'
),
(
	'Fusce tempor nibh sit amet dolor posuere, quis dignissim augue egestas',
	'fusce-tempor-nibh',
	'Suspendisse potenti. Curabitur quis purus gravida, tristique
	 nulla maximus, convallis eros. Phasellus fringilla congue
	 volutpat. Fusce non ex vitae lacus condimentum sagittis.
	 Vivamus ullamcorper tincidunt malesuada. Suspendisse pretium
	 tellus in quam iaculis imperdiet. Suspendisse potenti.
	 Praesent luctus posuere venenatis. Phasellus efficitur metus
	 congue est finibus, a laoreet enim imperdiet. In hac habitasse
	 platea dictumst.',
	'2013-01-07',
	'2013-01-08',
	FALSE,
	'PUBLISHED',
	'2013-01-07'
),
(
	'Fusce nec tristique nisi',
	'fusce-nec-tristique',
	'Sed sit amet condimentum justo. Duis sed lorem eu odio dictum
	 laoreet. Fusce sit amet lacus viverra, imperdiet nisi quis,
	 laoreet dui. Nulla lobortis tortor eu mauris tempus porta.
	 Vestibulum euismod erat et elementum vulputate. Suspendisse
	 porta arcu leo, sed mollis orci lobortis vel. Aenean a feugiat
	 ex. Cras vitae odio tortor.',
	'2013-02-11',
	'2013-02-20',
	TRUE,
	'PUBLISHED',
	'2013-02-23'
),
(
	'Morbi euismod lacus a est euismod blandit',
	'morbi-euismod-lacus',
	'Cras ultricies tempus lectus, ac rutrum nisl pulvinar id.
	 Donec tincidunt aliquam erat id ornare. In luctus a augue in
	 faucibus. Mauris eget massa eget libero malesuada suscipit ut
	 a neque. Duis quis magna ac tellus volutpat tempor. Integer ac
	 purus in quam tincidunt blandit sed et arcu. Etiam massa
	 lorem, dictum eu sem id, maximus finibus urna. In feugiat
	 purus non imperdiet tempus. Fusce sed blandit ipsum. Duis
	 finibus, felis sit amet cursus tincidunt, velit ipsum
	 ullamcorper nibh, eget lobortis velit nisl eu tortor. Duis
	 vitae est feugiat, efficitur ligula ut, pellentesque libero.',
	'2013-03-05',
	'2013-03-10',
	TRUE,
	'PUBLISHED',
	'2013-03-15'
),
(
	'Donec eu mollis arcu, nec elementum neque',
	'donec-eu-mollis',
	'Donec fermentum euismod mauris, ut aliquet sapien convallis
	 et. Pellentesque bibendum, quam nec suscipit condimentum,
	 lacus tellus volutpat enim, sit amet interdum neque elit sit
	 amet lorem. Sed sed ornare sem. Vivamus eget aliquet nulla.
	 Fusce malesuada ligula sed arcu posuere ullamcorper in
	 faucibus massa. Fusce id libero scelerisque, maximus nibh
	 vitae, placerat dui. Proin ac ante molestie, pretium erat et,
	 finibus tellus.',
	'2013-07-01',
	'2013-07-07',
	TRUE,
	'PUBLISHED',
	'2013-07-05'
),
(
	'Morbi rutrum',
	'morbi-rutrum',
	'Mauris dapibus et velit sit amet varius. Donec id sapien in
	 elit varius fermentum at eget mauris. In hac habitasse platea
	 dictumst. Maecenas venenatis, eros quis pretium tincidunt,
	 arcu velit pellentesque nulla, eget rutrum lorem lectus ac
	 metus. Curabitur rutrum, nunc eu porta lacinia, dolor lorem
	 laoreet nibh, vel mollis risus nibh ut magna. Morbi a finibus
	 massa. Donec commodo cursus rutrum. Praesent eu eleifend orci.
	 In porttitor neque a erat tempus, nec porta justo consectetur.
	 Phasellus mattis et turpis vitae vulputate. Vestibulum id
	 velit ac lacus commodo congue. Praesent aliquam consectetur
	 nibh a gravida. Donec et magna nec diam egestas eleifend.
	 Nulla auctor tellus at odio sollicitudin ultricies. Curabitur
	 volutpat vehicula arcu eget tempus.',
	'2013-11-05',
	'2013-11-12',
	TRUE,
	'DRAFT',
	NULL
),
(
	'Suspendisse augue dolor, eleifend eu purus in, luctus rutrum eros',
	'suspendisse-augue-dolor',
	'In fermentum tellus sit amet varius eleifend. Mauris vitae
	 venenatis risus. Pellentesque ut lectus ipsum. Nunc id est in
	 sapien dapibus feugiat. Cras vitae tellus nec nulla posuere
	 facilisis quis in sem. Ut in hendrerit nulla. Aliquam
	 malesuada mollis neque vitae efficitur. Nullam id facilisis
	 diam, a sollicitudin orci.',
	'2014-02-03',
	'2014-02-17',
	FALSE,
	'PUBLISHED',
	'2014-02-21'
),
(
	'Pellentesque eget ligula diam',
	'pellentesque-eget-ligula-diam',
	'Morbi odio nulla, auctor at arcu eu, suscipit vulputate
	 lectus. Maecenas lacus justo, condimentum a imperdiet eget,
	 tincidunt at massa. In ac urna neque. In hac habitasse platea
	 dictumst. Suspendisse imperdiet in augue in rhoncus. Aenean
	 eleifend, diam sit amet auctor maximus, velit nulla tristique
	 diam, et semper orci ex non risus. Praesent massa dui,
	 bibendum vel augue sit amet, consectetur tempus tellus.',
	'2014-05-26',
	'2014-05-29',
	TRUE,
	'PUBLISHED',
	'2014-05-29'
),
(
	'Donec pretium magna non condimentum posuere',
	'donec-pretium-magna',
	'Integer nec velit ut felis hendrerit ultricies. Ut suscipit
	 blandit rutrum. In et rutrum justo. Curabitur posuere
	 facilisis eros, vel eleifend massa vulputate ut. Vestibulum
	 nec augue suscipit velit gravida laoreet. Fusce vel euismod
	 nisi. Duis ullamcorper nibh ac est elementum dictum. Nullam
	 posuere, diam vel ultricies pulvinar, lectus arcu vestibulum
	 ante, in imperdiet leo augue pharetra odio. Integer sed tempor
	 leo. Sed in semper eros.',
	'2014-07-01',
	'2014-07-01',
	TRUE,
	'PUBLISHED',
	'2014-08-02'
),
(
	'Mauris felis quam, vehicula lacinia sem ut, porttitor semper nulla',
	'mauris-felis-quam',
	'Vestibulum ac egestas ex. Vestibulum convallis, justo a
	 tristique efficitur, nisi elit molestie ligula, quis congue
	 sem nibh a tortor. In fringilla, risus eget cursus finibus,
	 nulla velit venenatis neque, non ullamcorper libero orci quis
	 enim. Nullam lacus nibh, porttitor non orci eu, egestas
	 posuere tortor. Vestibulum scelerisque dui in ante
	 sollicitudin laoreet. Fusce et libero nunc. Aenean vel nisi
	 molestie, consequat ligula et, finibus velit. Vestibulum
	 lobortis fermentum quam, et imperdiet turpis rhoncus vel.
	 Vestibulum quis tincidunt nibh, quis fermentum dui. Nulla
	 facilisi. Nunc dapibus aliquam urna, sed vulputate magna
	 dictum a. Suspendisse enim elit, lacinia sed volutpat sed,
	 iaculis et sapien. Quisque imperdiet quam dictum, iaculis
	 tortor in, dapibus enim.',
	'2015-02-13',
	NULL,
	TRUE,
	'PUBLISHED',
	'2015-02-16'
),
(
	'Donec erat risus, varius vitae velit non, finibus faucibus odio',
	'donec-erat-risus',
	'Mauris dapibus dui vitae tempor viverra. Pellentesque sed nibh
	 at velit volutpat vulputate vitae non nulla. Donec vehicula
	 quam eget risus dictum molestie. Sed efficitur pulvinar elit,
	 in laoreet justo ullamcorper ac. Cras scelerisque convallis
	 dapibus. Vivamus sed laoreet quam. Integer vel dapibus urna.
	 Nullam sed eleifend turpis, in dictum lorem. Nam quis semper
	 elit.',
	'2015-02-13',
	NULL,
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2015-11-16'
),
(
	'Vestibulum et nisl mauris',
	'vestibulum-et-nisl-mauris',
	'Phasellus iaculis dui non enim imperdiet ornare. Suspendisse
	 gravida nec diam vitae vehicula. Praesent at imperdiet leo,
	 quis viverra ante. Praesent lacinia ultrices metus, nec aliquam
	 mi rutrum sit amet. Sed in urna et metus sollicitudin posuere.
	 Maecenas sed arcu porta, feugiat nisi quis, fermentum massa.
	 Proin maximus vitae mi vitae egestas. Aenean pellentesque arcu
	 sagittis finibus molestie. Ut gravida elit et orci ullamcorper
	 elementum. In diam felis, dapibus et condimentum non, porttitor
	 ut tortor. Proin at dolor eget est iaculis iaculis. Etiam in
	 nisi lorem.',
	'2015-02-13',
	'2016-01-01',
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2015-11-16'
),
(
	'Fusce bibendum cursus nisi, a maximus ligula tincidunt et',
	'fusce-bibendum-cursus',
	'Morbi egestas accumsan fringilla. Nam at pretium nibh. In
	 consectetur, arcu a laoreet elementum, diam quam imperdiet
	 ipsum, quis suscipit nibh orci vel dui. Etiam mattis finibus
	 tincidunt. Aliquam non ante gravida, dignissim justo ac,
	 faucibus felis. Phasellus tincidunt fermentum fermentum.
	 Suspendisse ultrices odio at lectus elementum tempor imperdiet
	 quis urna. Etiam a tellus felis. Nunc sodales dolor nunc, eget
	 tempus ipsum auctor eleifend. Phasellus a semper lorem.
	 Suspendisse eleifend non ex vitae efficitur.',
	'2015-02-13',
	'2016-01-01',
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2015-11-16'
),
(
	'Cras non molestie mauris',
	'cras-non-molestie-mauris',
	'Morbi egestas accumsan fringilla. Nam at pretium nibh. In
	 consectetur, arcu a laoreet elementum, diam quam imperdiet
	 ipsum, quis suscipit nibh orci vel dui. Etiam mattis finibus
	 tincidunt. Aliquam non ante gravida, dignissim justo ac,
	 faucibus felis. Phasellus tincidunt fermentum fermentum.
	 Suspendisse ultrices odio at lectus elementum tempor imperdiet
	 quis urna. Etiam a tellus felis. Nunc sodales dolor nunc, eget
	 tempus ipsum auctor eleifend. Phasellus a semper lorem.
	 Suspendisse eleifend non ex vitae efficitur.',
	'2015-02-13',
	'2016-01-01',
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2015-11-16'
),
(
	'Aenean in magna augue',
	'aenean-in-magna-augue',
	'Fusce egestas porttitor mauris at molestie. Vestibulum ante
	 ipsum primis in faucibus orci luctus et ultrices posuere cubilia
	 Curae; Vestibulum eget feugiat diam. Curabitur eu elit ullamcorper,
	 molestie nibh eu, auctor leo. Pellentesque habitant morbi
	 tristique senectus et netus et malesuada fames ac turpis
	 egestas. Maecenas dapibus elit vitae dui posuere aliquet.
	 Pellentesque convallis, arcu sit amet pretium luctus, lacus
	 turpis efficitur tortor, vel rutrum quam sem et lectus.',
	'2015-02-13',
	'2016-01-01',
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2050-04-21'
),
(
	'Integer sit amet lectus ac ligula aliquet aliquam',
	'integer-sit-amet-lectus',
	'Cras consectetur sapien non erat imperdiet ornare. Curabitur
	 at lacus non ex vestibulum vestibulum a viverra dui. Aenean ut
	 arcu pulvinar, posuere ante ut, convallis lorem. Duis nisl
	 nibh, tempus id urna ut, bibendum interdum lacus. Etiam
	 venenatis venenatis semper. Ut suscipit sapien ut elit
	 bibendum pretium.',
	'2015-02-13',
	'2016-01-01',
	TRUE,
	'PUBLICATION_SCHEDULED',
	'2050-04-21'
),
(
	'Curabitur convallis dui metus, in congue erat luctus vel',
	'curabitur-convallis-dui',
	'Donec nisi erat, consectetur at ullamcorper ut, ullamcorper
	 vitae ipsum. Proin et eros arcu. Interdum et malesuada fames
	 ac ante ipsum primis in faucibus. Phasellus sapien justo,
	 accumsan eget enim vitae, tempus porttitor sapien. Nam sagittis
	 nec ante ac porttitor. Nullam sollicitudin congue imperdiet.
	 Quisque ac condimentum ex, ac lobortis libero. In orci elit,
	 vulputate sed neque et, gravida pretium nisi. In hac habitasse
	 platea dictumst. Maecenas faucibus, nibh vitae pharetra
	 bibendum, est nibh luctus nunc, sit amet maximus nulla ante in
	 justo. Cras tellus arcu, tempus id eleifend quis, facilisis at
	 dolor. Quisque pulvinar eu erat in ultricies.',
	'2015-02-13',
	'2016-01-01',
	FALSE,
	'PUBLICATION_SCHEDULED',
	'2050-04-21'
);
