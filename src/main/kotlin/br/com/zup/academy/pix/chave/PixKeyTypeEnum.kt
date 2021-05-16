package br.com.zup.academy.pix.chave

enum class PixKeyTypeEnum(val domainType: TipoChaveEnum?) {

    CPF(domainType = TipoChaveEnum.CPF),
    CNPJ(domainType = null),
    PHONE(domainType = TipoChaveEnum.TELEFONE),
    EMAIL(domainType = TipoChaveEnum.EMAIL),
    RANDOM(domainType = TipoChaveEnum.ALEATORIA);

    companion object {
        private val mapping: Map<TipoChaveEnum?, PixKeyTypeEnum> =
            PixKeyTypeEnum.values().associateBy(PixKeyTypeEnum::domainType)

        fun by(domainTypeValue: TipoChaveEnum): PixKeyTypeEnum {
            return mapping[domainTypeValue] ?: throw IllegalArgumentException("O tipo da chave PIX é inválido.")
        }
    }

}