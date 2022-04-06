package pt.ipleiria.estg.dei.pi.mymultiprev.data.network.mappers

interface EntityMapper<DTO, Entity> {
    fun mapFromDTO(dto: DTO): Entity

    fun mapFromEntity(entity: Entity): DTO
}